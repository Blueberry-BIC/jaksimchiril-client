package com.example.bicapplication

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.service.autofill.Validators.or
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import com.example.bicapplication.MainActivity.Companion.userId
import com.example.bicapplication.SelectedchallActivity.Companion.challData
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
    private lateinit var userId: String
    private var adminAddr: String = ""
    private val model: LiveDataViewModel by viewModels()
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

        dataStoreModule = DataStoreModule(applicationContext)

        lifecycleScope.launch {
            userId = dataStoreModule.userIdData.first()
            Log.d("dataStore", "[Participate] user_id: " + userId)

        }

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
                checkParticipant()
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
    private fun checkParticipant() {
        challData?.let {
            Log.d("Participate", "chall_list = ${it.userList}")
            if ((it.userList == null) or (it.userList?.get(0).isNullOrEmpty())) {
                // userlist 존재하지 않음
                // 무조건 참가 가능
                Log.d("Participate", "check1: ${userId}")
                if (it.money == 0){
                    participate(userId, challData?.challId!!)
                    moveMainAct()
                } else{
                    sendKlay(it.money.toString())
                }
            } else {
                // userlist가 null이 아니고, 빈 값도 없는
                // user가 포함되는지 확인이 필요
                if (it.userList!!.indexOf(userId) == -1) {
                    // user 미존재 - 아직 참여하지 않은 경우
                    Log.d("Participate", "check2: ${userId}")
                    if (it.money == 0){
                        participate(userId, challData?.challId!!)
                        moveMainAct()
                    } else{
                        sendKlay(it.money.toString())
                    }
                } else {
                    // user 이미 존재 - 이미 참여했으나 어쩌다가 여기까지 넘어와진 경우
                    Log.d("Participate", "check3 ${userId}")
                    Toast.makeText(this@ParticipateActivity, "이미 참가한 챌린지입니다.", Toast.LENGTH_SHORT).show()
                    moveMainAct()
                }
            }
        }
    }

    private fun participate(userId:String, challId:String){
        // 참여하는 경우 -> api 호출
        // 디비 userlist에 push
        retrofitInterface.participate(challId, userId).enqueue(object:Callback<StringData>{
            override fun onResponse(call: Call<StringData>, response: Response<StringData>) {
                if (response.isSuccessful){
                    Log.d("PARTICIPATE", "success 참가 ${response.body()}")
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

    private fun sendKlay(fee:String){
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
                        Log.d("Participate", "register 시작 ${userId}")
                        participate(userId, challData?.challId!!)
                        moveMainAct()
                    }else{
                        moveMainAct()
                    }
                }
                else{
                    Log.e("SEND", "success send result, but ${response.errorBody()}")
                    moveMainAct()
                }
            }
            //통신 실패했을 때
            override fun onFailure(call: Call<SendResultData>, t: Throwable) {
                Log.e("SEND", "fail result :  ${t}")
                moveMainAct()
            }
        })
    }

    private fun moveMainAct(){
        val intent = Intent(this@ParticipateActivity, MainActivity::class.java) ///MainActivity
        startActivity(intent)
        finish()
    }


    companion object {
        var challData: ChallData? = null
    }
}