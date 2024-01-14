package com.example.bicapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.bicapplication.databinding.ActivityNewchallBinding

class NewchallActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewchallBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewchallBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}