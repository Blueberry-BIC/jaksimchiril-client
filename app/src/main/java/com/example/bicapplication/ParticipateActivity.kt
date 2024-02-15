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
    val currentDepositData: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
}

class ParticipateActivity : AppCompatActivity() {
    private lateinit var binding: ActivityParticipateBinding
    private var adminAddr: String = ""
    private var challDeposit: String = ""
    private val model: LiveDataViewModel by viewModels()
    private val challid = StringData("65b53c4d9e61e58ebbcc08c8") // 일단 하드코딩
    private val useridforchall = IntData(4) //일단 하드코딩. 추후 string 타입으로 변환 필요
    private val userid = StringData("65b537f388bb8423ff6e0f8d")

    val retrofitInterface = RetrofitInterface.create("http://192.168.136.1:8081/")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityParticipateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val walletObserver = Observer<String>{
                newWalletData -> binding.textviewParticipateWalletaddr.text = newWalletData
        }
        val depositObserver = Observer<String>{
                newDepositData -> binding.textviewParticipateDeposit.text = newDepositData
        }

        model.currentWalletData.observe(this, walletObserver)
        model.currentDepositData.observe(this, depositObserver)

        binding.apply {
            getAdminWalletAddr()
            getOneChall()
            btnParticipateExit.setOnClickListener {
                val intent = Intent(this@ParticipateActivity, MainActivity::class.java) ///MainActivity
                startActivity(intent)
            }
            btnParticipateParticipate.setOnClickListener {
                participate()
                Toast.makeText(this@ParticipateActivity, "참가 완료되었습니다", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@ParticipateActivity, MainActivity::class.java) ///MainActivity
                startActivity(intent)
            }
        }
    }

    // admin wallet address 가져와서 화면에 출력
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

    // selectedchall로부터 chall id를 넘겨받아 예치금 화면에 출력
    private fun getOneChall(){
        /*val challDBManager = ChallDBManager()
        val chall = challDBManager.getOneChallInfo(challid)

        challDeposit = chall.money.toString()
        Log.d("PARTICIPATE", "${challDeposit}")
        model.currentDepositData.value = challDeposit + " KLAY"*/
        retrofitInterface.getOneChallInfo(challid.stringData).enqueue(object : Callback<DepositData>{
            override fun onResponse(call: Call<DepositData>, response: Response<DepositData>) {
                if (response.isSuccessful){
                    Log.d("PARTICIPATE", "success : ${response.body()}")
                    val res = response.body()!!
                    challDeposit = res.depositData.toString()
                    model.currentDepositData.value = challDeposit + " KLAY"
                }
            }

            override fun onFailure(call: Call<DepositData>, t: Throwable) {
                Log.d("PARTICIPATE","fail with ${t}")
            }
        })
    }

    private fun participate(){
        retrofitInterface.patchActivatedChall(challid.stringData, useridforchall).enqueue(object: Callback<StringData>{
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
    }
}