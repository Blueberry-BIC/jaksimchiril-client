package com.example.bicapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.bicapplication.databinding.ActivityNewchallBinding
import android.widget.ArrayAdapter

class NewchallActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewchallBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewchallBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //dropdown menu
        val categoryItems = resources.getStringArray(R.array.category_items)
        val arrayAdapter = ArrayAdapter(this, R.layout.dropdown_category_item, categoryItems)
        binding.autocomepletetextviewNewchallCategory.setAdapter(arrayAdapter)

        //취소하기 버튼 -> MainActivity로 이동
        binding.btnNewchallExit.setOnClickListener {
            val intent = Intent(this@NewchallActivity, MainActivity::class.java)
            startActivity(intent)
        }
    }
}