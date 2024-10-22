package com.example.bicapplication.certify

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.bicapplication.GlobalVari
import com.example.bicapplication.databinding.ActivityNewsCertifyBinding
import com.example.bicapplication.manager.DataStoreModule
import com.example.bicapplication.responseObject.BooleanResponse
import com.example.bicapplication.responseObject.ItNewsResponse
import com.example.bicapplication.retrofit.RetrofitInterface
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

//시사뉴스 인증화면 -> 최신 it뉴스 서버가 크롤링해와서 문제 출제
class NewsCertifyActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewsCertifyBinding

    //서버가 it뉴스 크롤링 후 여기로 전달해줄 데이터 목록
    private var title:String = ""  //뉴스 제목
    private var contents:String = ""  //소내용
    private var media:String = ""  //언론사
    private var time:String = ""  //작성시간
    private var url:String = ""  //url

    //기사 contents를 빈칸 뚫어서 저장할 string과 정답값
    private var problem:String = ""
    private var answer:String = ""

    private lateinit var challId: String

    //datastore에서 값 가져오기 위한 변수
    private lateinit var userid: String
    private lateinit var dataStoreModule: DataStoreModule


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewsCertifyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dataStoreModule = DataStoreModule(this)
        //datastore에서 값 가져오기
        lifecycleScope.launch {
            userid = dataStoreModule.userIdData.first()
            if (userid.isNotBlank()) {
                lifecycleScope.cancel()
            }
        }

        //selectedchall액티비티로부터 전달 받은 챌id, 종료일
        val intent = intent
        challId = intent.getStringExtra("challId").toString()

        getNewsInfo()

        //답안제출 버튼 클릭시
        binding.certifyButton.setOnClickListener {
            val userAnswer = binding.ActionEditText.text.toString() //유저가 입력한 답안
            if(userAnswer==answer) { //유저가 정답인 경우
                plusSuccessCount() //성공횟수 1증가
            }else{  //오답인 경우
                Toast.makeText(this, "틀렸습니다.", Toast.LENGTH_SHORT).show()
                moveActivity(false)
            }
        }
    }


    //서버로부터 크롤링한 it뉴스정보 가져오기
    private fun getNewsInfo(){
        val retrofitInterface = RetrofitInterface.create(GlobalVari.getUrl())
        retrofitInterface.getNews().enqueue(object : Callback<ItNewsResponse> {
            override fun onFailure(
                call: Call<ItNewsResponse>,
                t: Throwable
            ) {
                moveActivity(false)
                Log.e("태그", "it뉴스 통신 아예실패  ,t.message: " + t.message)
            }
            @SuppressLint("SetTextI18n")
            override fun onResponse(
                call: Call<ItNewsResponse>,
                response: Response<ItNewsResponse>
            ) {
                if (response.isSuccessful) {
                    //Log.e("태그", "it뉴스 통신 성공, "+response.body() )
                    title = response.body()?.title!!
                    contents = response.body()?.contents!!
                    media = response.body()?.media!!
                    time = response.body()?.time!!
                    url = response.body()?.url!!

                    answer = contents.substring(30, 37) //문제의 정답값
                    val blank="□□□□□□□" //정답값 빈칸갯수 7개
                    problem = "${contents.substring(0, 30)}$blank${contents.substring(37, contents.lastIndex)}" //문제 문자열

                    binding.QuizTextView.text = "다음은 [${media}] 언론사에서 ${time}에 올라온 기사의 일부분입니다.\n\n" +
                                                "기사 제목: [${title}]\n\n" +
                                                "해당 기사를 읽고 아래 빈칸에 알맞은 말을 채워넣으면 성공입니다!(띄워쓰기 포함)\n\n" +
                                                "-----------------------------\n(기사 내용중)\n\n" + problem

                    binding.InfoTextView.text = "- 기사는 네이버뉴스 [IT/과학]탭에서 확인할 수 있습니다." +
                                                 "\n- 기사 url: ${url}"

                } else {
                    moveActivity(false)
                    Log.e(
                        "태그",
                        "it뉴스 요청 서버접근했지만 실패: response.errorBody()?.string()" + response.errorBody().toString()
                    )
                }
            }
        })
    }


    //인증이 다 끝난후 성공여부 데이터를 인증현황 액티비티로 전달 및 이동을 위한 메소드
    private fun moveActivity(success:Boolean,){
        val intent = Intent(this@NewsCertifyActivity, CertifyStatusActivity::class.java)
        intent.putExtra("성공여부", success)
        intent.putExtra("시사뉴스인증방문", true)
        setResult(4,intent)
        finish()
    }

    //챌린지 인증 성공시 DB의 성공횟수 1증가 로직
    private fun plusSuccessCount() {
        Toast.makeText(this, "정답입니다.", Toast.LENGTH_SHORT).show()
        val retrofitInterface = RetrofitInterface.create(GlobalVari.getUrl())
        retrofitInterface.putSuccess(userid, challId).enqueue(object : Callback<String> {
            override fun onFailure(
                call: Call<String>,
                t: Throwable
            ) {
                Log.e("태그", "통신 아예실패  ,t.message: " + t.message)
            }

            override fun onResponse(
                call: Call<String>,
                response: Response<String>
            ) {
                if (response.isSuccessful) {
                    Log.e("태그", "성공횟수 증가 통신 성공: , response.body():" + response.body())
                    moveActivity(true)
                } else {
                    Log.e(
                        "태그",
                        "서버접근했지만 실패: response.errorBody()?.string()" + response.errorBody()
                            .toString()
                    )
                }
            }
        })
    }






}