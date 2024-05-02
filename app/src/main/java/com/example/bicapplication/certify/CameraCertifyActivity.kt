package com.example.bicapplication.certify

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.bicapplication.GlobalVari
import com.example.bicapplication.MainActivity
import com.example.bicapplication.databinding.ActivityCameraCertifyBinding
import com.example.bicapplication.manager.DataStoreModule
import com.example.bicapplication.retrofit.RetrofitInterface
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

//이미지인증을 수행하는 액티비티
class CameraCertifyActivity : AppCompatActivity() {
    // storage 권한 처리에 필요한 변수
    private val camera = arrayOf(Manifest.permission.CAMERA)
    private val camera_mode = 98

    private lateinit var binding: ActivityCameraCertifyBinding
    private lateinit var bitmap:Bitmap
    private lateinit var userid: String

    private lateinit var dataStoreModule: DataStoreModule
    private lateinit var encryptImageActivity: EncryptImageActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraCertifyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dataStoreModule = DataStoreModule(applicationContext)
        encryptImageActivity = EncryptImageActivity()

//        lifecycleScope.launch {
//            userid = dataStoreModule.userIdData.first()
//            if (userid.isNotBlank()) {
//                Log.d("encImg", "[Camera] userid: " + userid)
//                lifecycleScope.cancel()
//            }
//        }

        callCamera()
//        encryptTest()

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

    private fun decodeHex(input: String): ByteArray {
        check(input.length % 2 == 0) { "Must have an even length" }

        val byteIterator = input.chunkedSequence(2)
            .map { input.toLong(16).toByte() }
            .iterator()

        return ByteArray(input.length / 2) { byteIterator.next() }
    }

    // 이미지 암호화
    private fun encryptTest() {
        val key = "1334eea06ea6e2f99e002d72d7f03158be8f0489654dcb5aad3a58a9cd50476e"
        val byteKey = decodeHex(key)
        Log.d("encImg", "byteKey: " + byteKey)
        val iv = encryptImageActivity.setIv()
        val enc = encryptImageActivity.encrypt("asdfghjk", byteKey, iv)
        val dec = encryptImageActivity.decrypt(enc, byteKey, iv)
        Log.d("encImg", "iv: " + iv)
        Log.d("encImg", "enc: " + enc)
        Log.d("encImg", "dec: " + dec)
    }

    private fun getGroupKey(userId: String) {
        val retrofitInterface = RetrofitInterface.create(GlobalVari.getUrl())   //10.0.2.2
        retrofitInterface.getGroupKey(userId).enqueue(object :
            Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    Log.d("encImg", "success getGroupKey ${response.body()}")
                    CameraCertifyActivity.groupKey = response.body().toString()
                    Log.d("encImg", "success2 getGroupKey " + CameraCertifyActivity.groupKey)

                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.d("encImg", "fail getGroupKey ${t}")
            }
        })
    }


    companion object {
        var groupKey: String? = null
    }

}