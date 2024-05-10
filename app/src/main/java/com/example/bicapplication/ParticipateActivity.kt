package com.example.bicapplication

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import com.example.bicapplication.databinding.ActivityParticipateBinding
import com.example.bicapplication.datamodel.*
import com.example.bicapplication.klaytn.*
import com.example.bicapplication.manager.DataStoreModule
import com.example.bicapplication.retrofit.RetrofitInterface
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LiveDataViewModel: ViewModel() {
    val currentWalletData: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
}

class ParticipateActivity : AppCompatActivity() {
    private lateinit var binding: ActivityParticipateBinding
    private lateinit var dataStoreModule: DataStoreModule
    private lateinit var request_key:String  //카이카스 지갑주소 받기위해 필요한 key값

    private var adminAddr: String = ""
    //private var userid: String = ""
    private val model: LiveDataViewModel by viewModels()
    private val userid = "65e3b68de9562a3a91d247ca" // 지금은 임의 설정 -> 추후 usermanager를 통해 받을 수 있도록 수정 필요
    private var check_request = false //유저가 카이카스 지갑주소 가져오기 auth 진행 2단계인 request까지해서 카이카스앱 오픈했는지 체크
    val retrofitInterface = RetrofitInterface.create(GlobalVari.getUrl())
    val retrofitInterface2 = RetrofitInterface.create("https://api.kaikas.io/api/v1/k/")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityParticipateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val walletObserver = Observer<String>{
                newWalletData -> binding.textviewParticipateWalletaddr.text = newWalletData
        }
        model.currentWalletData.observe(this, walletObserver)

        /*dataStoreModule = DataStoreModule(applicationContext)

        lifecycleScope.launch {
            if (userid.isNotBlank()){
                userid = dataStoreModule.userIdData.first()
                Log.d("dataStore", "[Main] wallet_addr: " + userid)
            }
        }*/

        binding.apply {
            // 사용자 화면 출력을 위해 관리자 지갑 주소와 챌린지 참가비 가져오기
            getAdminWalletAddr()
            textviewParticipateDeposit.text = challData?.money.toString()

            //참가 취소 -> main으로 돌아가기
            btnParticipateExit.setOnClickListener {
                val intent = Intent(this@ParticipateActivity, MainActivity::class.java) ///MainActivity
                startActivity(intent)
            }

            //참가하기 -> db 정보 업데이트, 참가비 송금 완료된 이후 main으로 돌아가기
            btnParticipateParticipate.setOnClickListener {
                participate()
                sendKlay()
                val intent = Intent(this@ParticipateActivity, MainActivity::class.java) ///MainActivity
                startActivity(intent)
            }
        }
    }

    override fun onResume() {
        //유저가 2단계 과정인 request단계 진행해서 카이카스앱 연동까지 확인되면 result단계 진행
        if(check_request){
            Log.e("SEND", "Participate에서 onResume으로 result 시작")
            result(request_key)
        }
        super.onResume()
    }

    //participate
    private fun participate(){
        challData?.let {
            if (it.userNum == 1){
                // user_list 데이터 새로 만들어서 필드 추가 필요한 경우
                val userList = mutableListOf<String>(userid)
                it.userNum = userList.size
                it.userList = userList
                Log.d("CHECK", "check1 ${it}")
            }
            else {
                try{
                    if (it.userList!!.indexOf(userid) == -1) {
                        // user 미존재 - 아직 참여하지 않은 경우
                        it.userList!!.add(userid)
                        it.userNum = it.userList!!.size
                        Log.d("CHECK", "check2 ${it}")
                    }
                    else {
                        // user 이미 존재 - 이미 참여했으나 여기까지 넘어와진 경우 - 이전에 예외처리 필요
                        Log.d("CHECK", "check3 ${it}")
                        Toast.makeText(this@ParticipateActivity, "이미 참가한 챌린지입니다.", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@ParticipateActivity, MainActivity::class.java) ///MainActivity
                        startActivity(intent)
                    }
                } catch (e:Exception){
                    Log.d("CHECK4", "EXCEPTION: ${e}")
                }
            }
            // participate 서버 api 동작 (1) - activated_chall collection update
            retrofitInterface.putUserList(it.challId.toString(), it).enqueue(object : Callback<StringData>{
                override fun onResponse(
                    call: Call<StringData>,
                    response: Response<StringData>
                ) {
                    if (response.isSuccessful){
                        Log.d("PARTICIPATE", "success put user_list ${response.body()}")
                        Toast.makeText(this@ParticipateActivity, response.body()!!.stringData, Toast.LENGTH_SHORT).show()
                    }
                    else {
                        Log.d("PARTICIPATE", "fail1 with ${response.errorBody()}")
                    }
                }

                override fun onFailure(call: Call<StringData>, t: Throwable) {
                    Log.d("PARTICIPATE", "fail2 with ${t}")
                }
            })

            // participate 서버 api 동작 (2) - user collection update
            retrofitInterface.patchProgressChall(userid, StringData(it.challId.toString())).enqueue(object: Callback<StringData>{
                override fun onResponse(call: Call<StringData>, response: Response<StringData>) {
                    if (response.isSuccessful){
                        Log.d("PARTICIPATE", "success patch progress_chall: ${response.body()}")
                        Toast.makeText(this@ParticipateActivity, response.body()!!.stringData, Toast.LENGTH_SHORT).show()
                    }
                    else {
                        Log.d("PARTICIPATE", "fail with ${response.errorBody()}")
                    }
                }
                override fun onFailure(call: Call<StringData>, t: Throwable) {
                    Log.d("PARTICIPATE", "fail with ${t}")
                }
            })
        }

    }

    // admin wallet address DB에서 가져와서 화면에 출력
    private fun getAdminWalletAddr() {
        retrofitInterface.getAdminWalletAddr().enqueue(object:Callback<WalletData>{
            override fun onResponse(
                call: Call<WalletData>,
                response: Response<WalletData>
            ) {
                if (response.isSuccessful){
                    Log.d("PARTICIPATE", "success get walletaddr ${response.body()}")
                    val res = response.body()!!
                    adminAddr = res.walletAddr
                    model.currentWalletData.value = adminAddr
                }
                else {
                    Log.d("PARTICIPATE", "fail with ${response.errorBody()}")
                }
            }
            override fun onFailure(call: Call<WalletData>, t: Throwable) {
                Log.d("PARTICIPATE", "fail with ${t}")
            }
        })
    }

    private fun sendKlay(){
        var fee = if (challData!=null) challData!!.money.toString() else "1"
        var sendData = AuthData("send_klay", AuthData.Bapp("BIC"), AuthData.TransactionData(adminAddr, fee))

        retrofitInterface2.reqSend(sendData).enqueue(object : Callback<PrepareRespData>{
            override fun onResponse(
                call: Call<PrepareRespData>,
                response: Response<PrepareRespData>
            ) {
                if (response.isSuccessful){
                    request_key = response.body()?.request_key.toString()
                    Log.d("SEND", "participate에서 prepare 성공: ${request_key}")
                    request(request_key)
                }
                else {
                    Log.d("SEND", "보낸것: ${sendData}")
                    Log.d("SEND", "prepare 실패: ${response.body()}")
                }
            }

            override fun onFailure(call: Call<PrepareRespData>, t: Throwable) {
                Log.d("SEND", "participate에서 prepare 실패")
            }
        })
    }

    private fun request(reqkey:String) {
        try {
            Log.e("result: ", "move to app")
            val urlScheme = "kaikas://wallet/api?request_key=${reqkey}"
            val intent = Intent()
            intent.action = Intent.ACTION_VIEW
            intent.addCategory(Intent.CATEGORY_BROWSABLE)
            intent.addCategory(Intent.CATEGORY_DEFAULT)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.data = Uri.parse(urlScheme)
            startActivity(intent)  //여기서 카이카스앱 실행되고 그 앱에서 유저에게 BIC와 연동할지 물어봄
            check_request = true //유저가 BIC앱 돌아왔을때 메인액티비티로 이동하기 위함
        } catch (e: ActivityNotFoundException){
            Log.e("REQUEST", "fail to request. ${e}")
            val packageName = "io.klutch.wallet" //카이카스지갑앱 패키지네임
            // 플레이 스토어로 이동
            startActivity(
                Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")
                )
            )
            finish()
        }
    }

    private fun result(reqkey:String){
        retrofitInterface2.sendResult(reqkey).enqueue(object : Callback<SendResultData> {
            override fun onResponse(call: Call<SendResultData>, response: Response<SendResultData>){
                //통신 성공했을 때
                if(response.isSuccessful){
                    Log.e("result결과값 태그","response: "+response.body())
                    val signed_tx = response.body()?.result?.signed_tx
                    val tx_hash = response.body()?.result?.tx_hash

                    if(signed_tx!=null){
                        Log.d("result결과값 태그", "signed_tx :"+response.body()?.result?.signed_tx)

                        val intent = Intent(this@ParticipateActivity, MainActivity::class.java)
                        //intent.putExtra("wallet_addr",wallet_addr)
                        startActivity(intent)
                        finish()
                    }else{
                        moveParticipateAct()
                    }
                }
                else{
                    Log.e("SEND", "success send result, but ${response.errorBody()}")
                    moveParticipateAct()
                }
            }
            //통신 실패했을 때
            override fun onFailure(call: Call<SendResultData>, t: Throwable) {
                Log.e("SEND", "fail result :  ${t}")
                moveParticipateAct()
            }
        })
    }

    private fun moveParticipateAct(){
        val intent = Intent(this@ParticipateActivity, Connect2KlaytnActivity::class.java) ///MainActivity
        startActivity(intent)
        finish()
    }


    companion object {
        var challData: ChallData? = null
    }
}