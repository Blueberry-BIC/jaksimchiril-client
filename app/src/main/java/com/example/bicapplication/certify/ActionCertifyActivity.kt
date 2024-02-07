package com.example.bicapplication.certify

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.Toast
import com.example.bicapplication.R
import com.example.bicapplication.databinding.ActivityActionCertifyBinding
import com.example.bicapplication.databinding.ActivityCertifyStatusBinding
import com.example.bicapplication.responseObject.ActionQuiz
import com.example.bicapplication.retrofit.RetrofitInterface
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.properties.Delegates
import kotlin.system.exitProcess

//액션인증을 수행하는 액티비티
class ActionCertifyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityActionCertifyBinding

    //퀴즈정보 담을 변수들
    private lateinit var problem:String
    private lateinit var answer:String
    private var limitedTime by Delegates.notNull<Int>()
    private lateinit var category: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityActionCertifyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //서버에게 액션퀴즈 하나 랜덤으로 가져오기 요청 보내기
        getActionQuiz()
    }



    //서버에게 액션퀴즈 하나 가져오는 요청보냄
    @SuppressLint("SuspiciousIndentation")
    private fun getActionQuiz(){
        val retrofitInterface = RetrofitInterface.create("http://10.0.2.2:8081/")
        retrofitInterface.requestAction().enqueue(object : Callback<ActionQuiz> {
            override fun onFailure(
                call: Call<ActionQuiz>,
                t: Throwable
            ) {
                Log.e("태그", "액션퀴즈요청 통신 아예실패  ,t.message: " + t.message)
                //Toast.makeText(this@PostActivity, "댓글 업로드 실패", Toast.LENGTH_SHORT).show()
            }
            override fun onResponse(
                call: Call<ActionQuiz>,
                response: Response<ActionQuiz>
            ) {
                if (response.isSuccessful) {
                      //List<Any>타입으로 받은 data값을 jsonarray로 다시 만들어줌
                      val jsonArray = JSONArray(response.body()?.result)
                      val jsonObject = jsonArray.getJSONObject(0)

                      answer = jsonObject.getString("answer")
                      problem = jsonObject.getString("problem")
                      category = jsonObject.getString("category")
                      limitedTime = jsonObject.getInt("limited_time")

                      //퀴즈 보여주고 타이머 시작
                      showQuiz()

                      Log.e("태그", "액션퀴즈요청 통신 성공, "+jsonObject.getString("problem"))
                    //Toast.makeText(this@PostActivity, "입력되었습니다.", Toast.LENGTH_SHORT).show()
                } else {
                    Log.e(
                        "태그",
                        "액션퀴즈요청 서버접근했지만 실패: response.errorBody()?.string()" + response.errorBody().toString()
                    )
                    //Toast.makeText(this@PostActivity, "댓글 업로드 실패", Toast.LENGTH_SHORT).show()
                }
            }
        })
    } //getActionQuiz()

    //서버에서 받아온 데이터들로 퀴즈정보들 뷰에 보여줌
    private fun showQuiz(){
        binding.QuizTextView.text = problem

        timer()

        //답안제출버튼 클릭시
        binding.certifyButton.setOnClickListener {
            //editText의 유저입력값을 가져옴
            var userAnswer = binding.ActionEditText.text.toString()
            userAnswer = userAnswer.trim()
            if(userAnswer == answer){  //정답일 경우
                //내 인증현황 업데이트 등 처리해주기

                Toast.makeText(this, "정답입니다!", Toast.LENGTH_SHORT).show()
                finish()
            }else if(userAnswer==""){
                Toast.makeText(this, "답안을 작성하세요.", Toast.LENGTH_SHORT).show()

            }else{//오답
                //내 인증현황 업데이트 등 처리해주기

                Toast.makeText(this, "오답입니다.", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }


    //타이머 기능
   private fun timer(){
        limitedTime *= 1000  //db에서 가져온 초단위 제한시간값을 밀리세컨드 단위로 변경해줌
        val mCountDown: CountDownTimer = object : CountDownTimer(limitedTime.toLong(), 1000) {
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                // countDownInterval 마다 호출 (여기선 1000ms)
                binding.timerTextView.text = "남은 시간: " + millisUntilFinished / 1000
            }
            @SuppressLint("SetTextI18n")
            override fun onFinish() {
                // 타이머가 종료되면 호출
                binding.certifyButton.isClickable = false  //제출버튼 비활성화
                Toast.makeText(this@ActionCertifyActivity, "시간이 종료되었습니다.", Toast.LENGTH_SHORT).show()
                finish()
            }
        }.start()
    }





}