package com.example.bicapplication

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.bicapplication.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.apply {
            addBtn.setOnClickListener {
                val intent = Intent(requireActivity(), NewchallActivity::class.java)
                startActivity(intent)
            }
        }
        return binding.root
    }

    companion object {
        fun newInstance() : HomeFragment {
            return HomeFragment()
        }
    }
}