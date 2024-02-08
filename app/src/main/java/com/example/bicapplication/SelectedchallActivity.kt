package com.example.bicapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.bicapplication.databinding.ActivitySelectedchallBinding
import com.example.bicapplication.datamodel.ChallData

class SelectedchallActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySelectedchallBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectedchallBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            btnSelectedBack.setOnClickListener {
                finish()
            }
        }
    }

    companion object {
        var challData: ChallData? = null
    }

}