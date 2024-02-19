package com.example.bicapplication.certify

import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.bicapplication.R
import com.example.bicapplication.databinding.ActivityActionCertifyBinding
import com.example.bicapplication.databinding.ActivityGithubCertifyBinding
import com.example.bicapplication.responseObject.ActionQuiz
import com.example.bicapplication.responseObject.BooleanResponse
import com.example.bicapplication.retrofit.RetrofitInterface
import org.json.JSONArray
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

//깃허브 커밋여부 인증을 수행하는 액티비티
class GithubCertifyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGithubCertifyBinding

    //서버가 깃허브 크롤링 후 여기로 전달해줄값 목록
    private var is_success:Boolean = false  //성공여부
    private var commitDate:String = "" //가장 최근 커밋날짜
    private var commitRepo:String = ""  //커밋한 repo이름

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGithubCertifyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //인증하기버튼 클릭시
        binding.certifyButton.setOnClickListener {
            //유저가 입력한 깃허브id값 가져옴
            var githubId = binding.GithubEditText.text.toString()
            githubId = githubId.trim()
            Log.e("태그", "githubId: "+githubId)
            if(githubId.isNotEmpty()){
                getMyCommitInfo(githubId) //서버에게 깃허브정보 크롤링 요청
            }
        }
    }


    //서버로부터 깃허브 커밋여부와 마지막 커밋날짜 가져옴
    private fun getMyCommitInfo(githubId : String){
        val retrofitInterface = RetrofitInterface.create("http://10.0.2.2:8081/")
        retrofitInterface.getIsCommitted(githubId).enqueue(object : Callback<BooleanResponse> {
            override fun onFailure(
                call: Call<BooleanResponse>,
                t: Throwable
            ) {
                moveActivity(false, "받아오지못함", "받아오지못함")
                Log.e("태그", "깃허브커밋 통신 아예실패  ,t.message: " + t.message)
            }
            override fun onResponse(
                call: Call<BooleanResponse>,
                response: Response<BooleanResponse>
            ) {
                if (response.isSuccessful) {
                    Log.e("태그", "깃허브커밋 통신 성공, "+response.body() )
//                    Toast.makeText(this@GithubCertifyActivity, "인증결과: "+response.body()?.is_committed
//                        +"\n마지막 커밋날짜:"+response.body()?.lastcommitday +"\n커밋한 repo이름:"+ response.body()?.commitRepo , Toast.LENGTH_SHORT).show()
                    //Toast.makeText(this@GithubCertifyActivity, "인증성패여부", Toast.LENGTH_SHORT).show()
                    is_success = response.body()?.is_committed!!  //성공여부 저장
                    commitDate = response.body()?.lastcommitday!! //최신 커밋날짜 저장
                    commitRepo = response.body()?.commitRepo!! //커밋한 레포 저장
                    moveActivity(is_success, commitDate, commitRepo)
                } else {
                    moveActivity(false, "받아오지못함", "받아오지못함")
                    Log.e(
                        "태그",
                        "깃허브커밋요청 서버접근했지만 실패: response.errorBody()?.string()" + response.errorBody().toString()
                    )
                }
            }
        })
    } //getIsCommitted


    //인증이 다 끝난후 성공여부 데이터를 인증현황 액티비티로 전달 및 이동을 위한 메소드
    private fun moveActivity(success:Boolean, commitDate: String, commitRepo:String){
        val intent = Intent(this@GithubCertifyActivity, CertifyStatusActivity::class.java)
        intent.putExtra("성공여부", success)
        intent.putExtra("커밋날짜", commitDate)
        intent.putExtra("커밋레포", commitRepo)
        intent.putExtra("깃허브인증방문", true)
        startActivity(intent)
        finish()
    }





}