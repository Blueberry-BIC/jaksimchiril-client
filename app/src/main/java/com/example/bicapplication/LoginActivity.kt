package com.example.bicapplication

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.example.bicapplication.databinding.ActivityLoginBinding
import com.example.bicapplication.klaytn.Connect2KlaytnActivity
import com.example.bicapplication.klaytn.ResultRespData
import com.example.bicapplication.manager.DataApplication
import com.example.bicapplication.manager.DataStoreModule
import com.example.bicapplication.retrofit.RetrofitInterface
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var dataStoreModule: DataStoreModule  // 자동로그인을 위한 dataStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dataStoreModule = DataStoreModule(applicationContext)

        try {
            // 로그인된 상태인지 확인 (자동로그인)
//            CoroutineScope(Dispatchers.IO).launch {
            lifecycleScope.launch {
                val wallet = dataStoreModule.walletAddrData.first()
                Log.d("dataStore", "[Login] wallet: " + wallet)

                if (wallet.isNullOrBlank()) {
                    lifecycleScope.cancel()
                } else {
                    val intent = Intent(this@LoginActivity, MainActivity::class.java) ///MainActivity
                    startActivity(intent)
                }
            }
        } catch (e: Exception) {
            Log.e("dataStore", e.localizedMessage)
        }

        initLayout()

    }

    private fun initLayout() {
        //로그인 -> Connect2Klaytn으로 이동
        binding.apply {
            imagebtnKaikasLogin.setOnClickListener {
                val intent = Intent(this@LoginActivity, Connect2KlaytnActivity::class.java) ///MainActivity
                startActivity(intent)  //Connect2KlaytnActivity
                //finish()
            }
        }
    }


}