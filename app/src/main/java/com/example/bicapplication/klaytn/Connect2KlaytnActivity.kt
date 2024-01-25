package com.example.bicapplication.klaytn

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.bicapplication.databinding.ActivityConnect2KlaytnBinding
import com.example.bicapplication.retrofit.RetrofitInterface
import retrofit2.*

class Connect2KlaytnActivity : AppCompatActivity() {
    private lateinit var binding:ActivityConnect2KlaytnBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConnect2KlaytnBinding.inflate(layoutInflater)
        setContentView(binding.root)
        connect2kaikas()

    }

    private fun connect2kaikas() {
        val reqAuth = AuthData("auth", AuthData.Bapp("BIC"))
        val retrofitInterface = RetrofitInterface.create("https://api.kaikas.io/api/v1/k/")
        var request_key = ""

        //prepare - request - result
        //retrofit 객체 만들어서 method 사용
        retrofitInterface.requestAuth(reqAuth).enqueue(object : Callback<PrepareRespData> {
            override fun onResponse(call: Call<PrepareRespData>, response: Response<PrepareRespData>){
                //통신 성공했을 때
                if(response.isSuccessful){
                    Log.d("AUTH", "success auth ${response}")
                    request_key = response.body()?.request_key.toString()
                    request(request_key)
                }
                else{
                    Log.d("AUTH", "success auth, but ${response.errorBody()}")
                }
            }
            //통신 실패했을 때
            override fun onFailure(call: Call<PrepareRespData>, t: Throwable) {
                Log.d("AUTH", "fail ${t}")
            }

        })
    }

    //deeplink to kaikas app
    private fun request(reqkey:String) {
        try {
            val uri = Uri.parse("kaikas://wallet/api?request_key=${reqkey}")
            startActivity(Intent(Intent.ACTION_VIEW, uri))
        } catch (e:Exception){
            Log.d("REQUEST", "fail to request. ${e}")
        }
    }

}