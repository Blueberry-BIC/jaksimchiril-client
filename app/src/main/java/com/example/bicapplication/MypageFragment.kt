package com.example.bicapplication

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.example.bicapplication.databinding.FragmentHomeBinding
import com.example.bicapplication.databinding.FragmentMypageBinding
import com.example.bicapplication.manager.DataStoreModule
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MypageFragment : Fragment() {
    private lateinit var binding: FragmentMypageBinding
    private lateinit var dataStoreModule: DataStoreModule

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMypageBinding.inflate(inflater, container, false)

        dataStoreModule = DataStoreModule(requireContext())

        lifecycleScope.launch {
//            val userName = dataStoreModule.userNameData.first()
//            binding.name.text = userName
            val userId = dataStoreModule.userIdData.first()
            Log.d("dataStore", "[Mypage] user_id: " + userId)
            binding.id.text = userId
            val walletAddr = dataStoreModule.walletAddrData.first()
            Log.d("dataStore", "[Mypage] wallet addr: " + walletAddr)
            binding.wallet.text = walletAddr
        }

        binding.logout.setOnClickListener {
            // 로그아웃 시 dataStore의 userId 값 삭제
        }

        binding.Withdrawal.setOnClickListener {
            // 회원 탈퇴 시 User DB에서 해당 사용자 정보 삭제
        }


        return binding.root
    }

    companion object {
        fun newInstance() : MypageFragment {
            return MypageFragment()
        }
    }
}