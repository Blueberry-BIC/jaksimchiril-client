package com.example.bicapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.bicapplication.databinding.ActivityFirstUserInfoBinding
import com.example.bicapplication.datamodel.StringData
import com.example.bicapplication.datamodel.UserData
import com.example.bicapplication.manager.DataStoreModule
import com.example.bicapplication.responseObject.UserPostResponse
import com.example.bicapplication.retrofit.RetrofitInterface
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FirstUserInfoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFirstUserInfoBinding
    private lateinit var dataStoreModule: DataStoreModule  // 자동로그인을 위해 깃허브 아이디와 닉네임을 저장할 dataStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFirstUserInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        dataStoreModule = DataStoreModule(applicationContext)

        binding.apply {
            btnFirstuserinfoSave.setOnClickListener {
                // 사용자 입력 확인
                if (edittextFirstuserinfoGithubid.text.toString().isNullOrBlank() or edittextFirstuserinfoNickname.text.toString().isNullOrBlank()){
                    Toast.makeText(this@FirstUserInfoActivity, "빈 칸을 입력해주세요", Toast.LENGTH_SHORT).show()
                }
                else {
                    // 사용자 지갑 주소, 닉네임, github id를 서버로 보내서 DB에 저장
                    // 서버 API 사용 - 사용자 정보 디비 저장 - POST
                    var githubid = edittextFirstuserinfoGithubid.text.toString()
                    var nickname = edittextFirstuserinfoNickname.text.toString()
                    registerUser(githubid, nickname)
                }
            }
        }
    }

    private fun registerUser(githubid: String, nickname: String){
        var new_user = UserData(nickname, WalletData, githubid)
        val retrofitInterface = RetrofitInterface.create(GlobalVari.getUrl())

        retrofitInterface.postUser(new_user).enqueue(object: Callback<StringData>{
            override fun onResponse(
                call: Call<StringData>,
                response: Response<StringData>
            ) {
                if (response.isSuccessful){
                    Log.d("REGISTER_USER", "success put new user: ${response.body()}")
                    // datastore에 닉네임과 깃허브 아이디 저장
                    lifecycleScope.launch {
                        dataStoreModule.saveWalletAddr(WalletData!!)
                        dataStoreModule.saveUserName(nickname)
                        dataStoreModule.saveGithubId(githubid)
                    }
                    Toast.makeText(this@FirstUserInfoActivity, response.body()!!.stringData+"등록 되었습니다.", Toast.LENGTH_SHORT).show()
                    // main으로 이동
                    val intent = Intent(this@FirstUserInfoActivity, MainActivity::class.java) ///MainActivity
                    startActivity(intent)
                }
                else {
                    Log.d("REGISTER_USER", "fail with ${response.errorBody()}")
                }
            }
            override fun onFailure(call: Call<StringData>, t: Throwable) {
                Log.d("REGISTER_USER", "fail with ${t}")
            }
        })
    }

    companion object {
        var WalletData: String? = null
    }
}