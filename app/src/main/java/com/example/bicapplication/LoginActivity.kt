package com.example.bicapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.bicapplication.databinding.ActivityLoginBinding
import com.example.bicapplication.klaytn.Connect2KlaytnActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //로그인 -> Connect2Klaytn으로 이동
        binding.apply {
            imagebtnKaikasLogin.setOnClickListener {
                val intent = Intent(this@LoginActivity, Connect2KlaytnActivity::class.java) ///MainActivity
                startActivity(intent)
            }
        }
    }
}