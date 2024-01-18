package com.example.bicapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.bicapplication.databinding.ActivityFinishedchallBinding

class FinishedchallActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFinishedchallBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFinishedchallBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            btnFinishedBack.setOnClickListener {
                finish()
            }
        }
    }
}