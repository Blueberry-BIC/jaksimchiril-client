package com.example.bicapplication

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

        val categoryItems = resources.getStringArray(R.array.category_items)
        val arrayAdapter = ArrayAdapter(this, R.layout.dropdown_category_item, categoryItems)
        binding.categoryAutocomepletetextview.setAdapter(arrayAdapter)
    }
}