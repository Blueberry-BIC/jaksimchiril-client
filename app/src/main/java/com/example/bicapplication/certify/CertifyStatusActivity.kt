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
import androidx.datastore.dataStore
import com.example.bicapplication.GlobalVari
import com.example.bicapplication.R
import com.example.bicapplication.databinding.ActivityCertifyStatusBinding
import com.example.bicapplication.manager.DataStoreModule
import com.example.bicapplication.retrofit.RetrofitInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


//진행중인 챌린지의 인증현황을 보여주는 액티비티
class CertifyStatusActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCertifyStatusBinding
    private var adapter: CertifyStatusAdapter? = null
    private var certifyDataList: Array<CertifyData>? = null
    private lateinit var challId: String
    private lateinit var endDate: String

    //인증화면(이미지, 액션, 깃허브) 갔다올때 성공여부 등 데이터 전달받기위함
    private val activityResultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {

        /**
        이미지인증 결과 받아옴
         */
        if (it.resultCode == 1) {
            val intent = it.data
            val bitmap = intent?.getParcelableExtra<Bitmap>("사진")
            val visited = intent?.getBooleanExtra("이미지인증방문", false) //이미지인증을 갔다온건지 확인위함
            binding.imageView.visibility = View.GONE
            //이미지 인증을 갔다왔으면 진행
            if (visited == true) {
                Log.e("태그,", "bitmap:" + bitmap)
                if (bitmap != null) {  //올린 사진이 있을경우(인증 완료인 경우)
                    binding.imageView.visibility = View.VISIBLE
                    binding.imageView.setImageBitmap(bitmap)
                    binding.successTextView.visibility = View.GONE
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
            val visited = intent?.getBooleanExtra("액션인증방문", false) //깃허브인증을 갔다온건지 확인위함
            val is_success = intent?.getBooleanExtra("성공여부", false)

            //액션 인증 갔다가 여기로 온거라면 진행
            if (visited == true) {
                Log.e("태그,", "is_success:" + is_success)

                var success = ""
                if (is_success == true) {
                    success = "성공"
                    //DB의 activiated_chall에 해당 유저의 is_sucesss필드에다가 성공횟수 +1 하기
                    plusSuccessCount()

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
                } else {
                    success = "실패"


                }
                binding.imageView.visibility = View.GONE
                binding.successTextView.text =
                    "$success\n최신 커밋 날짜: $commitDate\n커밋한 레포: $commitRepo\n레포URL: https://github.com/로컬에 저장된 유저깃허브id값 넣기/$commitRepo "
            }
        }
    }


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCertifyStatusBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //mychall프래그먼트로부터 전달 받은 챌id와 종료기간
        val intent = intent
        challId = intent.getStringExtra("challId").toString()
        endDate = intent.getStringExtra("endDate").toString()

        binding.TimeTextView.text = "종료 시간:$endDate"

        certifyDataList = arrayOf(
            CertifyData("aa", R.drawable.bic_logo),
            CertifyData("bb", R.drawable.bic_logo),
            CertifyData("cc", R.drawable.bic_logo),
            CertifyData("dd", R.drawable.bic_logo),
            CertifyData("ee", R.drawable.bic_logo)
        )


        //인증하기 버튼 클릭시 -> 액션, 깃허브, 이미지 인증 3개중 한개 페이지 이동
        binding.certifyButton.setOnClickListener {
            val intent = Intent(
                this,
                ActionCertifyActivity::class.java
            )  //ActionCertifyActivity  //GithubCertifyActivity  //CameraCertifyActivity
            activityResultLauncher.launch(intent)
        }

        adapter = CertifyStatusAdapter(this, certifyDataList)
        binding.gridView.adapter = adapter
    }


    //챌린지 인증 성공시 DB의 성공횟수 1증가 로직
    private fun plusSuccessCount() {
        val userid = "65b537f388bb8423ff6e0f8d"//현재는 임의설정. 이후엔 datastore값 이용 예정
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
}