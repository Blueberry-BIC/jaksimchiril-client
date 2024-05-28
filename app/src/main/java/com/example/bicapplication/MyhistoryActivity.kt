package com.example.bicapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.bicapplication.databinding.ActivityMyhistoryBinding

class MyhistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyhistoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyhistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.myhistoryCard.setOnClickListener {
            val intent = Intent(this@MyhistoryActivity, ShowPrizeStatusActivity::class.java)
            startActivity(intent)
        }
    }

}