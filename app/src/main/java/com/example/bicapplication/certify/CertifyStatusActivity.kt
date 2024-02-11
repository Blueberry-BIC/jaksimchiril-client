package com.example.bicapplication.certify

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.bicapplication.R
import com.example.bicapplication.databinding.ActivityCertifyStatusBinding


//진행중인 챌린지의 인증현황을 보여주는 액티비티
class CertifyStatusActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCertifyStatusBinding
    private var adapter: CertifyStatusAdapter? = null
    private var certifyDataList: Array<CertifyData>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCertifyStatusBinding.inflate(layoutInflater)
        setContentView(binding.root)

        certifyDataList = arrayOf(
            CertifyData("aa", R.drawable.bic_logo),
            CertifyData("bb",R.drawable.bic_logo),
            CertifyData("cc",R.drawable.bic_logo),
            CertifyData("dd",R.drawable.bic_logo),
            CertifyData("ee",R.drawable.bic_logo)
        )

        //인증하기 버튼 클릭시 -> 액션, 깃허브, 이미지 인증 3개중 한개 페이지 이동
        binding.certifyButton.setOnClickListener {
            val intent = Intent(this, ActionCertifyActivity::class.java)  //ActionCertifyActivity  //GithubCertifyActivity  //CameraCertifyActivity
            startActivity(intent)
            finish()
        }

        // 1.이미지 인증 액티비티(cameraCertifyActivity)로부터 내가 찍은 사진 받아와서 뷰에 보여줌
        getMyPicture()

        // 2.깃허브 인증 액티비티로부터 받아온 깃허브 크롤링 결과값 받아와서 뷰에 보여줌
        getMyGithub()

        // 3.액션 인증 액티비티로부터 받아온 성공여부 결과값 받아와서 뷰에 보여줌
        getActionResult()

        adapter = CertifyStatusAdapter(this, certifyDataList)
        binding.gridView.adapter = adapter
    }


    /**
    이미지 인증 관련 메소드
     */
    //여기에 나중에 db로부터 인증 이미지들 가져올때 만약 이미지 존재하면 그 값등으로 바꾸기
    private fun getMyPicture(){
        val intent = intent  //이미지 인증 액티비티를 마친 후 전달받은 데이터
        val bitmap = intent.getParcelableExtra<Bitmap>("사진")
        val visited = intent.getBooleanExtra("이미지인증방문",false) //이미지인증을 갔다온건지 확인위함
        binding.imageView.visibility = View.GONE

        if(visited){    //이미지 인증을 갔다왔으면 진행
            Log.e("태그,", "bitmap:"+bitmap)
            if(bitmap!=null){
                binding.imageView.visibility = View.VISIBLE
                binding.imageView.setImageBitmap(bitmap)
            }else{  //아직 이미지 인증 안했거나, 다른 인증인 경우
                //binding.imageView.visibility = View.GONE
            }
        }
    }

    /**
    깃허브 인증 관련 메소드
     */
    //여기에 나중에 db로부터 깃허브 인증 가져올때 존재하면 그 값으로 등 바꾸기
    @SuppressLint("SetTextI18n")
    private fun getMyGithub(){
        val intent = intent  //깃허브 인증 액티비티 마친 후 전달받은 데이터
        val visited = intent.getBooleanExtra("깃허브인증방문",false) //깃허브인증을 갔다온건지 확인위함
        val is_success = intent.getBooleanExtra("성공여부",false)
        val commitDate = intent.getStringExtra("커밋날짜")
        val commitRepo = intent.getStringExtra("커밋레포")

        //깃허브 인증 갔다가 여기로 온거라면 진행
        if(visited){
            Log.e("태그,", "is_success:"+is_success)
            Log.e("태그,", "commitDate:"+commitDate)
            Log.e("태그,", "commitRepo:"+commitRepo)

            val success = if (is_success) "인증완료" else "미인증"
            binding.successTextView.text = "$success\n커밋날짜: $commitDate\n레포이름: $commitRepo"
        }
    }

    /**
    액션 인증 관련 메소드
     */
    //여기에 나중에 db로부터 깃허브 인증 가져올때 존재하면 그 값으로 등 바꾸기
    @SuppressLint("SetTextI18n")
    private fun getActionResult(){
        val intent = intent  //깃허브 인증 액티비티 마친 후 전달받은 데이터
        val visited = intent.getBooleanExtra("액션인증방문",false) //깃허브인증을 갔다온건지 확인위함
        val is_success = intent.getBooleanExtra("성공여부",false)

        //깃허브 인증 갔다가 여기로 온거라면 진행
        if(visited){
            Log.e("태그,", "is_success:"+is_success)

            val success = if (is_success) "성공" else "실패"
            binding.successTextView.text = success
        }
    }




}