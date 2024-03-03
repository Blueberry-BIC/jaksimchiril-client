package com.example.bicapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import com.example.bicapplication.databinding.ActivityLoginBinding
import com.example.bicapplication.klaytn.Connect2KlaytnActivity
import com.example.bicapplication.klaytn.ResultRespData
import com.example.bicapplication.retrofit.RetrofitInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //로그인 -> Connect2Klaytn으로 이동
        binding.apply {
            imagebtnKaikasLogin.setOnClickListener {
                val intent = Intent(this@LoginActivity, MainActivity::class.java) ///MainActivity
                startActivity(intent)  //Connect2KlaytnActivity
                //finish()
            }
        }
    }


}