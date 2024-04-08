package com.example.bicapplication.certify

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.bicapplication.databinding.ActivityCameraCertifyBinding

//이미지인증을 수행하는 액티비티
class CameraCertifyActivity : AppCompatActivity() {
    // storage 권한 처리에 필요한 변수
    private val camera = arrayOf(Manifest.permission.CAMERA)
    private val camera_mode = 98

    private lateinit var binding: ActivityCameraCertifyBinding
    private lateinit var bitmap:Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraCertifyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        callCamera()
        // 다시찍기 버튼 클릭시
        binding.cameraButton.setOnClickListener {
            callCamera()
        }
        // 완료하기 버튼 클릭시
        binding.completeButton.setOnClickListener {
            //bitmap 사진 데이터를 담아서 인증현황 액티비티로 이동
            val intent = Intent(this, CertifyStatusActivity::class.java)
            intent.putExtra("사진", bitmap)
            intent.putExtra("이미지인증방문", true)
            setResult(1,intent)
            finish()
        }
    }

    // 카메라 권한, 저장소 권한
    // 요청 권한
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when(requestCode){
            camera_mode -> {
                for (grant in grantResults){
                    if(grant != PackageManager.PERMISSION_GRANTED){
                        Toast.makeText(this, "카메라 권한을 승인해 주세요", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    // 다른 권한등도 확인이 가능하도록
    private fun checkPermission(permissions: Array<out String>, type:Int):Boolean{
        for (permission in permissions){
            if(ContextCompat.checkSelfPermission(this, permission)
                != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, permissions, type)
                return false
            }
        }
        return true
    }

    //결과 가져오기
    private val activityResult : ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()){
        if(it.resultCode== RESULT_OK && it.data != null){
            //값 담기
            val extras = it.data!!.extras
            //bitmap으로 타입 변경
            bitmap = extras?.get("data") as Bitmap
            //화면에 보여주기
            binding.imageView.setImageBitmap(bitmap)
        }
    }

    // 카메라 촬영 - 권한 처리
    private fun callCamera(){
        if(checkPermission(camera, camera_mode) ){  //권한체크
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            activityResult.launch(intent)
        }
    }

}