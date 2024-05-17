package com.example.bicapplication

import android.content.Intent
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
import com.example.bicapplication.retrofit.RetrofitInterface
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MypageFragment : Fragment() {
    private lateinit var binding: FragmentMypageBinding
    private lateinit var dataStoreModule: DataStoreModule
    private lateinit var userId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMypageBinding.inflate(inflater, container, false)

        dataStoreModule = DataStoreModule(requireContext())

        lifecycleScope.launch {
            val userName = dataStoreModule.userNameData.first()
            binding.name.text = userName
            userId = dataStoreModule.userIdData.first()
            Log.d("dataStore", "[Mypage] user_id: " + userId)
            binding.id.text = userId
            val walletAddr = dataStoreModule.walletAddrData.first()
            Log.d("dataStore", "[Mypage] wallet addr: " + walletAddr)
            binding.wallet.text = walletAddr
        }

        binding.logout.setOnClickListener {
            // 로그아웃 시 dataStore의 userId 값 삭제
            lifecycleScope.launch {
                dataStoreModule.saveWalletAddr("")
                dataStoreModule.saveUserId("")
                val intent = Intent(requireActivity(), LoginActivity::class.java) ///MainActivity
                startActivity(intent)
            }

        }

        binding.Withdrawal.setOnClickListener {
            // 회원 탈퇴 시 User DB에서 해당 사용자 정보 삭제
            Log.d("dataStore", "call deleteUser ${userId}")
            deleteUser(userId)
            lifecycleScope.launch {
//                deleteUser(userId)
                dataStoreModule.saveUserId("")
                dataStoreModule.saveWalletAddr("")
                val intent = Intent(requireActivity(), LoginActivity::class.java)
                startActivity(intent)
            }
        }

        return binding.root
    }

    private fun deleteUser(userid: String) {
        val retrofitInterface = RetrofitInterface.create(GlobalVari.getUrl())   //10.0.2.2
        retrofitInterface.deleteUser(userid).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    Log.d("dataStore", "success1 deleteUser ${userid}")
                } else {
                    Log.d("dataStore", "success2 deleteUser ${userid}")
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.d("dataStore", "fail deleteUser")
            }
        })
    }

    companion object {
        fun newInstance() : MypageFragment {
            return MypageFragment()
        }
    }
}