package com.example.bicapplication

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.bicapplication.certify.CameraCertifyActivity
import com.example.bicapplication.certify.CertifyStatusActivity
import com.example.bicapplication.certify.GithubCertifyActivity
import com.example.bicapplication.databinding.FragmentMychallBinding

class MychallFragment : Fragment() {
    lateinit var binding:FragmentMychallBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=FragmentMychallBinding.inflate(inflater)


        return binding.root
    }

    companion object {
        fun newInstance() : MychallFragment {
            return MychallFragment()
        }
    }
}