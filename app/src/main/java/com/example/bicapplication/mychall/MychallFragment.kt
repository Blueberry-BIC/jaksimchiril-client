package com.example.bicapplication.mychall

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.bicapplication.databinding.FragmentMychallBinding

class MychallFragment : Fragment() {
    lateinit var binding: FragmentMychallBinding
    private var adapter: MychallAdapter? = null
    private var ChallengeList: Array<Challenge>? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=FragmentMychallBinding.inflate(inflater)

        // 그리드뷰 만들어주기위한 작업 //////////////////////////////
        ChallengeList = arrayOf(
            Challenge("코딩스택","챌린지1","인증방식1", "참여자수: 2", "만료기간: 0월 0일 00시 00분"),
            Challenge("코딩스택","챌린지1","인증방식1", "참여자수: 2", "만료기간: 0월 0일 00시 00분"),
            Challenge("코딩스택","챌린지1","인증방식1", "참여자수: 2", "만료기간: 0월 0일 00시 00분"),
            Challenge("코딩스택","챌린지1","인증방식1", "참여자수: 2", "만료기간: 0월 0일 00시 00분"),
            Challenge("코딩스택","챌린지1","인증방식1", "참여자수: 2", "만료기간: 0월 0일 00시 00분"),
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