package com.example.bicapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.bicapplication.databinding.ActivityShowPrizeStatusBinding

class ShowPrizeStatusActivity : AppCompatActivity() {
    private lateinit var binding: ActivityShowPrizeStatusBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowPrizeStatusBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}