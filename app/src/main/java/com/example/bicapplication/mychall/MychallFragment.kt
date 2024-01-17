package com.example.bicapplication.mychall

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.bicapplication.certify.CertifyData
import com.example.bicapplication.certify.CertifyStatusActivity
import com.example.bicapplication.certify.CertifyStatusAdapter
import com.example.bicapplication.databinding.FragmentMychallBinding

class MychallFragment : Fragment() {
    lateinit var binding:FragmentMychallBinding
    private var adapter: MychallAdapter? = null
    private var ChallengeList: Array<Challenge>? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=FragmentMychallBinding.inflate(inflater)

        // 그리드뷰 만들어주기위한 작업 //////////////////////////////
        ChallengeList = arrayOf(
            Challenge("챌린지1","내용1"),
            Challenge("챌린지2","내용2"),
            Challenge("챌린지3","내용3"),
            Challenge("챌린지4","내용4"),
            Challenge("챌린지5","내용5")
        )
        adapter = MychallAdapter(activity, ChallengeList)

        binding.gridView.adapter = adapter
        ////////////////////////////////


        /* 인증현황페이지 테스트 위한 작업
        binding.button.setOnClickListener {
            val intent = Intent(activity, CertifyStatusActivity::class.java)
            startActivity(intent)
        }
         */
        return binding.root
    }

    companion object {
        fun newInstance() : MychallFragment {
            return MychallFragment()
        }
    }
}