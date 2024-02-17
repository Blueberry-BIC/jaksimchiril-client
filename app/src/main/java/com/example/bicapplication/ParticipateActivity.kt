package com.example.bicapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.example.bicapplication.databinding.ActivityParticipateBinding
import com.example.bicapplication.datamodel.*
import com.example.bicapplication.retrofit.RetrofitInterface
import retrofit2.*

class LiveDataViewModel: ViewModel() {
    val currentWalletData: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
}

class ParticipateActivity : AppCompatActivity() {
    private lateinit var binding: ActivityParticipateBinding
    private var adminAddr: String = ""
    private val model: LiveDataViewModel by viewModels()
    private val userid = "65b537f388bb8423ff6e0f8d" // 지금은 임의 설정 -> 추후 usermanager를 통해 받을 수 있도록 수정 필요

    val retrofitInterface = RetrofitInterface.create("http://10.0.2.2:8081/")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityParticipateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val walletObserver = Observer<String>{
                newWalletData -> binding.textviewParticipateWalletaddr.text = newWalletData
        }
        model.currentWalletData.observe(this, walletObserver)

        binding.apply {
            getAdminWalletAddr()
            textviewParticipateDeposit.text = challData?.money.toString()

            btnParticipateExit.setOnClickListener {
                val intent = Intent(this@ParticipateActivity, MainActivity::class.java) ///MainActivity
                startActivity(intent)
            }
            btnParticipateParticipate.setOnClickListener {
                participate()
                //Toast.makeText(this@ParticipateActivity, "참가 완료되었습니다", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@ParticipateActivity, MainActivity::class.java) ///MainActivity
                startActivity(intent)
            }
        }
    }

    //participate
    private fun participate(){
        challData?.let {
            if (it.userNum == 0){
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
        retrofitInterface.getAdminWalletAddr().enqueue(object:Callback<AdminWalletData>{
            override fun onResponse(
                call: Call<AdminWalletData>,
                response: Response<AdminWalletData>
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
            override fun onFailure(call: Call<AdminWalletData>, t: Throwable) {
                Log.d("PARTICIPATE", "fail with ${t}")
            }
        })
    }

    /*private fun participate(){
        retrofitInterface.patchActivatedChall(cha, useridforchall).enqueue(object: Callback<StringData>{
            override fun onResponse(call: Call<StringData>, response: Response<StringData>) {
                if (response.isSuccessful){
                    Log.d("PARTICIPATE", "success patch activatedchall: ${response.body()}")
                }
            }

            override fun onFailure(call: Call<StringData>, t: Throwable) {
                Log.d("PARTICIPATE", "fail with ${t}")
            }
        })

        retrofitInterface.patchUser(userid.stringData, challid).enqueue(object: Callback<StringData>{
            override fun onResponse(call: Call<StringData>, response: Response<StringData>) {
                if (response.isSuccessful){
                    Log.d("PARTICIPATE", "success patch user: ${response.body()}")
                }
            }
            override fun onFailure(call: Call<StringData>, t: Throwable) {
                Log.d("PARTICIPATE", "fail with ${t}")
            }
        })
    }*/

    companion object {
        var challData: ChallData? = null
    }
}