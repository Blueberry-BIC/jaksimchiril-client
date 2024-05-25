package com.example.bicapplication.certify

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.lifecycleScope
import com.example.bicapplication.GlobalVari
import com.example.bicapplication.R
import com.example.bicapplication.databinding.ActivityCertifyStatusBinding
import com.example.bicapplication.datamodel.ChallData
import com.example.bicapplication.manager.DataStoreModule
import com.example.bicapplication.responseObject.ListResponseData
import com.example.bicapplication.retrofit.RetrofitInterface
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import org.json.JSONArray
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


//진행중인 챌린지의 인증현황을 보여주는 액티비티
class CertifyStatusActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCertifyStatusBinding
    private lateinit var adapter: CertifyStatusAdapter
    private var certifyDataList: ArrayList<CertifyData> = ArrayList()
    private lateinit var challId: String
    private lateinit var endDate: String
    private var myCertifyCount: Double = 0.0

    //datastore에서 값 가져오기 위한 변수
    private lateinit var userid: String
    private lateinit var dataStoreModule: DataStoreModule


    //인증화면(이미지, 액션, 깃허브, 시사뉴스, 걸음수) 갔다올때 성공여부 등 데이터 전달받기위함
    @SuppressLint("SetTextI18n")
    private val activityResultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        binding.imageView.visibility = View.GONE
        /**
        이미지인증 결과 받아옴
         */
        if (it.resultCode == 1) {
            val intent = it.data
            val bitmap = intent?.getParcelableExtra<Bitmap>("사진")
            val visited = intent?.getBooleanExtra("이미지인증방문", false) //이미지인증을 갔다온건지 확인위함
            //이미지 인증을 갔다왔으면 진행
            if (visited == true) {
                //Log.e("태그,", "bitmap:" + bitmap)
                if (bitmap != null) {  //올린 사진이 있을경우(인증 완료인 경우)
                    binding.imageView.visibility = View.VISIBLE
                    binding.imageView.setImageBitmap(bitmap)
                    binding.successTextView.visibility = View.GONE
                    //DB의 activiated_chall에 해당 유저의 is_sucesss필드에다가 성공횟수 +1 하기
                    plusSuccessCount()
                    myCertifyCount++ //인증횟수 1증가(뷰 재랜더링을 위해)
                } else {  //아직 이미지 인증 안했거나, 다른 인증인 경우
                    binding.imageView.visibility = View.GONE
                }
            }
        }

        /**
        액션인증 결과 받아옴
         */
        else if (it.resultCode == 2) {
            val intent = it.data
            val visited = intent?.getBooleanExtra("액션인증방문", false) //액션인증을 갔다온건지 확인위함
            val is_success = intent?.getBooleanExtra("성공여부", false)

            //액션 인증 갔다가 여기로 온거라면 진행
            if (visited == true) {
               // Log.e("태그,", "is_success:" + is_success)
                var success = ""
                if (is_success == true) {
                    success = "성공"
                    //DB의 activiated_chall에 해당 유저의 is_sucesss필드에다가 성공횟수 +1 하기
                    plusSuccessCount()
                    myCertifyCount++ //인증횟수 1증가(뷰 재랜더링을 위해)
                } else {
                    success = "실패"
                }
                binding.successTextView.text = success
            }
        }

        /**
        깃허브인증 결과 받아옴
         */
        else if (it.resultCode == 3) {
            val intent = it.data
            val visited = intent?.getBooleanExtra("깃허브인증방문", false) //깃허브인증을 갔다온건지 확인위함
            val is_success = intent?.getBooleanExtra("성공여부", false)
            val commitDate = intent?.getStringExtra("커밋날짜")
            val commitRepo = intent?.getStringExtra("커밋레포")

            //깃허브 인증 갔다가 여기로 온거라면 진행
            if (visited == true) {
                var success = ""
                if (is_success == true) {
                    success = "성공"
                    //DB의 activiated_chall에 해당 유저의 is_sucesss필드에다가 성공횟수 +1 하기
                    plusSuccessCount()
                    myCertifyCount++ //인증횟수 1증가(뷰 재랜더링을 위해)
                } else {
                    success = "실패"
                }
                //binding.imageView.visibility = View.GONE
                binding.successTextView.text =
                    "$success\n최신 커밋 날짜: $commitDate\n커밋한 레포: $commitRepo\n레포URL: https://github.com/로컬에 저장된 유저깃허브id값 넣기/$commitRepo "
            }
        }

        /**
        it시사뉴스인증 결과 받아옴
         */
        else if (it.resultCode == 4) {
            val intent = it.data
            val is_success = intent?.getBooleanExtra("성공여부", false)
            val visited = intent?.getBooleanExtra("시사뉴스인증방문", false)

            //시사뉴스인증 갔다가 여기로 온거라면 진행
            if (visited == true) {
                var success = ""
                if (is_success == true) {
                    success = "성공"
                    //DB의 activiated_chall에 해당 유저의 is_sucesss필드에다가 성공횟수 +1 하기
                    plusSuccessCount()
                    myCertifyCount++ //인증횟수 1증가(뷰 재랜더링을 위해)
                } else {
                    success = "실패"
                }
                //binding.imageView.visibility = View.GONE
                binding.successTextView.text = success
            }
        }
        /**
        걸음수인증 결과 받아옴
         */
        else if (it.resultCode == 5) {
            val intent = it.data
            val is_success = intent?.getBooleanExtra("성공여부", false)
            val visited = intent?.getBooleanExtra("걸음수인증방문", false)

            //시사뉴스인증 갔다가 여기로 온거라면 진행
            if (visited == true) {
                var success = ""
                if (is_success == true) {
                    success = "성공"
                    //DB의 activiated_chall에 해당 유저의 is_sucesss필드에다가 성공횟수 +1 하기
                    plusSuccessCount()
                    myCertifyCount++ //인증횟수 1증가(뷰 재랜더링을 위해)
                } else {
                    success = "실패"
                }
                //binding.imageView.visibility = View.GONE
                binding.successTextView.text = success
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()
        //인증화면에서 성공후 돌아왔을시 인증횟수 바로 재랜더링
        binding.textView9.text = myCertifyCount.toInt().toString() + "회"
    }


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCertifyStatusBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dataStoreModule = DataStoreModule(this)
        //datastore에서 값 가져오기
        lifecycleScope.launch {
            userid = dataStoreModule.userIdData.first()
            Log.e("태그@@@@@@@#########", "userid: " + userid)
            if (userid.isNotBlank()) {
                lifecycleScope.cancel()
            }
        }

        //selectedchall액티비티로부터 전달 받은 챌id, 종료일
        val intent = intent
        challId = intent.getStringExtra("challId").toString()

        Log.e("태그@@@@@@@#########", "challId: " + challId)
        endDate = intent.getStringExtra("endDate").toString()
        binding.TimeTextView.text = "종료 시간:$endDate"

        //현재 챌린지 정보 가져와서 참가자 이름, 인증횟수들 텍스트뷰에 채우기
        getChallInfo()

        //인증하기 버튼 클릭시 -> 액션, 깃허브, 이미지, 시사뉴스, 걸음수 인증 -> 5개중 한개 페이지 이동
        binding.certifyButton.setOnClickListener {
            val intent = Intent(
                this,
                NewsCertifyActivity::class.java
            )  //ActionCertifyActivity  //GithubCertifyActivity  //CameraCertifyActivity  //NewsCertifyActivity  //WalkingCertifyActivity
            activityResultLauncher.launch(intent)
        }
    }

    //챌린지 인증 성공시 DB의 성공횟수 1증가 로직
    private fun plusSuccessCount() {
        //val userid = userid//현재는 임의설정. 이후엔 datastore값 이용 예정
        //Log.e("태그", "userid: " + userid)
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
                    Log.e("태그", "challId: $challId")

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

    //챌린지id로 현재 챌린지 docu 가져와서 참가자들 이름, 인증횟수 넣어주기 (각 유저들 인증횟수, user_list 등 가져오기 위함)
    private fun getChallInfo() {
        val retrofitInterface = RetrofitInterface.create(GlobalVari.getUrl())
        retrofitInterface.getActivatedChall(challId).enqueue(object : Callback<ListResponseData> {
            override fun onFailure(
                call: Call<ListResponseData>,
                t: Throwable
            ) {
                Log.e("태그", "통신 아예실패  ,t.message: " + t.message)
            }

            @SuppressLint("SetTextI18n")
            override fun onResponse(
                call: Call<ListResponseData>,
                response: Response<ListResponseData>
            ) {
                if (response.isSuccessful) {
                    //List<Any>타입으로 받은 data값을 jsonarray로 다시 만들어줌
                    val jsonArray = JSONArray(response.body()?.result)
                    val jsonObject = jsonArray.getJSONObject(0) // 본인 유저정보 json으로 가져옴
                    val userList = jsonObject.getJSONArray("user_list")
                    //Log.e("태그", "인증횟수get 통신 성공: , userList:" + userList)

                    var num = 0
                    for (i in 0 until userList.length()) {
                        if (userList[i] == userid) {  //만약 내 userid일 경우 패스
                            //만약 본인이 인증 한번도 안해서 챌린지 docu의 필드값에 본인 userid값 없을 경우 예외처리
                            try {
                                val arr =  jsonObject.getJSONArray(userList[i] as String)

                                myCertifyCount = arr[0] as Double  // 인증횟수

                                binding.textView9.text = myCertifyCount.toInt().toString() + "회"
                                continue
                            } catch (_: JSONException) {
                                binding.textView9.text =  myCertifyCount.toInt().toString() + "회"
                                continue
                            }
                        }
                        //만약 참가자가 인증 한번도 안해서 챌린지 docu의 필드값에 해당 참가자 userid값 없을 경우 예외처리
                        try {
                            val arr =  jsonObject.getJSONArray(userList[i] as String)
                            val certifyCount = arr[0] as Double // 인증횟수
                            val userid = userList[i].toString().substring(0 until 7) //참가자 userid값을 잘라서 0부터 7까지만 저장
                            val username = CertifyData(
                                "참가자${num + 1}\n(${userid}..)",
                                certifyCount.toInt().toString() + "회",
                                R.drawable.bic_logo
                            )
                            certifyDataList.add(username)
                        } catch (_: JSONException) {
                            val userid = userList[i].toString().substring(0 until 7)
                            val username = CertifyData(
                                "참가자${num + 1}\n(${userid}..)",
                                "0회",
                                R.drawable.bic_logo
                            )
                            certifyDataList.add(username)
                        }
                        num++
                    }
                    adapter = CertifyStatusAdapter(this@CertifyStatusActivity, certifyDataList)
                    binding.gridView.adapter = adapter
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