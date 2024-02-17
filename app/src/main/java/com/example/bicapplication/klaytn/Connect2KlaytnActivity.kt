package com.example.bicapplication.klaytn

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.bicapplication.LoginActivity
import com.example.bicapplication.MainActivity
import com.example.bicapplication.databinding.ActivityConnect2KlaytnBinding
import com.example.bicapplication.retrofit.RetrofitInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

//여기서 실질적으로 회원가입/로그인 로직 진행
//카이카스 지갑 주소 연동을 위한, prepare - request - result  3단계 진행하는 액티비티
class Connect2KlaytnActivity : AppCompatActivity() {
    private lateinit var binding:ActivityConnect2KlaytnBinding

    private var check_request = false //유저가 카이카스 지갑주소 가져오기 auth 진행 2단계인 request까지해서 카이카스앱 오픈했는지 체크
    private lateinit var request_key:String  //카이카스 지갑주소 받기위해 필요한 key값

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConnect2KlaytnBinding.inflate(layoutInflater)
        setContentView(binding.root)
        connect2kaikas()
    }

    override fun onResume() {
        //유저가 2단계 과정인 request단계 진행해서 카이카스앱 연동까지 확인되면 result단계 진행
        if(check_request){
            Log.e("태그", "커넥트2 액티비티에서 onResume으로 result 시작")
            result(request_key)
        }
        super.onResume()
    }

    //prepare단계 진행  -> 성공하면 request_key값 얻음
    private fun connect2kaikas() {
        val reqAuth = AuthData("auth", AuthData.Bapp("BIC"))
        val retrofitInterface = RetrofitInterface.create("https://api.kaikas.io/api/v1/k/")
        //prepare - request - result
        //retrofit 객체 만들어서 method 사용
        retrofitInterface.requestAuth(reqAuth).enqueue(object : Callback<PrepareRespData> {
            override fun onResponse(call: Call<PrepareRespData>, response: Response<PrepareRespData>){
                //통신 성공했을 때
                if(response.isSuccessful){
                    Log.e("AUTH", "success auth ${response}")
                    Log.e("태그", "response"+response.body().toString())
                    request_key = response.body()?.request_key.toString()
                    request(request_key)
                }
                else{
                    moveLoginAct()
                    Log.e("AUTH", "success auth, but ${response.errorBody()}")
                }
            }
            //통신 실패했을 때
            override fun onFailure(call: Call<PrepareRespData>, t: Throwable) {
                moveLoginAct()
                Log.e("AUTH", "fail ${t}")
            }
        })
    }

    //request단계 진행  -> 성공하면 카이카스앱으로 이동. 지갑앱 없으면 스토어 이동     //deeplink to kaikas app
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
        } catch (e:ActivityNotFoundException){
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


    //result단계 진행 -> 성공하면 지갑주소 데이터 실어서 메인액티비티 이동
    private fun result(reqkey:String){
        val retrofitInterface = RetrofitInterface.create("https://api.kaikas.io/api/v1/k/")
        retrofitInterface.requestResult(reqkey).enqueue(object : Callback<ResultRespData> {
            override fun onResponse(call: Call<ResultRespData>, response: Response<ResultRespData>){
                //통신 성공했을 때
                if(response.isSuccessful){
                    Log.e("result결과값 태그","response: "+response.body())
                    val wallet_addr = response.body()?.result?.klaytn_address

                    if(wallet_addr!=null){ //받아온 지갑주소값이 있다면 진행
                        Log.e("result결과값 태그", "klaytn_address: "+response.body()!!.result.klaytn_address)

                        //유저데이터 서버로 전송(DB 추가를 위해) (서버에서 해당 유저데이터가 DB에 이미 존재하는지 확인)
                        registerUser()

                        //받아온 지갑주소값과 함께 메인액티비티로 이동
                        val intent = Intent(this@Connect2KlaytnActivity, MainActivity::class.java)  //ActionCertifyActivity  //GithubCertifyActivity  //CameraCertifyActivity
                        intent.putExtra("wallet_addr",wallet_addr)
                        startActivity(intent)
                        finish()
                    }else{ //받아온 지갑주소값이 없다면
                        moveLoginAct()
                    }
                }
                else{
                    Log.e("AUTH", "success auth result, but ${response.errorBody()}")
                    moveLoginAct()
                }
            }
            //통신 실패했을 때
            override fun onFailure(call: Call<ResultRespData>, t: Throwable) {
                Log.e("AUTH", "fail result :  ${t}")
                moveLoginAct()
            }
        })
    }
    //카이카스 api 연동과정 실패시 다시 로그인 화면 이동
    private fun moveLoginAct(){
        val intent = Intent(this@Connect2KlaytnActivity, LoginActivity::class.java) ///MainActivity
        startActivity(intent)
        finish()
    }


    //유저 신규 등록
    private fun registerUser(){

    }





}