package com.example.bicapplication.certify

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
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
import com.example.bicapplication.datamodel.ImageData
import com.example.bicapplication.manager.DataStoreModule
import com.example.bicapplication.retrofit.RetrofitInterface
import com.google.firebase.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

//이미지인증을 수행하는 액티비티
class CameraCertifyActivity : AppCompatActivity() {
    // storage 권한 처리에 필요한 변수
    private val camera = arrayOf(Manifest.permission.CAMERA)
    private val camera_mode = 98

    private lateinit var binding: ActivityCameraCertifyBinding
    private lateinit var bitmap:Bitmap
    private lateinit var userId: String
    private lateinit var challId: String
    private lateinit var imageUri: Uri
//    private lateinit var fbStorage: Firebase

    private lateinit var dataStoreModule: DataStoreModule
    private lateinit var encryptImageActivity: EncryptImageActivity
    val retrofitInterface = RetrofitInterface.create(GlobalVari.getUrl())   //10.0.2.2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraCertifyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dataStoreModule = DataStoreModule(applicationContext)
        encryptImageActivity = EncryptImageActivity()

        val intent = intent
        challId = intent.getStringExtra("challId").toString()

        lifecycleScope.launch {
            userId = dataStoreModule.userIdData.first()
            if (userId.isNotBlank()) {
                Log.d("encImg", "[Camera] userid: " + userId)
                lifecycleScope.cancel()
            }
        }

        Log.d("encImg", "${System.currentTimeMillis()}")

        callCamera()

        // 다시찍기 버튼 클릭시
        binding.cameraButton.setOnClickListener {
            callCamera()
        }
        // 완료하기 버튼 클릭시
        binding.completeButton.setOnClickListener {
            //Firebase에 사진 업로드
            Log.d("encImg", "[completeButton] URI: " + imageUri)
            uploadImage(imageUri,
                mSuccessHandler = { uri ->
                    Toast.makeText(this, "업로드에 성공했습니다", Toast.LENGTH_SHORT).show()
                    saveImage(uri)
                },
                mErrorHandler = {
                    Toast.makeText(this, "업로드에 실패했습니다", Toast.LENGTH_SHORT).show()
                }
            )
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
        getImageIntent(applicationContext)
        Log.d("encImg", "[activityResult] URI : ${imageUri}")
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


    private fun getImageIntent(context: Context): Intent {
        imageUri = Uri.EMPTY
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val contentValues = ContentValues()
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "${timeStamp}.jpg")
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        //'RELATIVE_PATH', RequiresApi Q
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)

        //URI 형식 EX) content://media/external/images/media/1006
        imageUri = context.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        ) ?: Uri.EMPTY
        Log.d("encImg", "[getImageIntent] URI: ${imageUri}")
        val fullSizeCaptureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        fullSizeCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        return fullSizeCaptureIntent
    }


    private fun decodeHex(input: String): ByteArray {
        check(input.length % 2 == 0) { "Must have an even length" }

        val byteIterator = input.chunkedSequence(2)
            .map { input.toLong(16).toByte() }
            .iterator()

        return ByteArray(input.length / 2) { byteIterator.next() }
    }

    private fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        var outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, outputStream)
        return outputStream.toByteArray()
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

    // 이미지를 Firebase에 업로드
    private fun uploadImage(
        imageURI: Uri,
        mSuccessHandler: (String) -> Unit,
        mErrorHandler: () -> Unit,
    ) {
        val storage = Firebase.storage
        val storageRef = storage.getReference("images")
        val imageFileName = "${System.currentTimeMillis()}.png"
        val mountainsRef = storageRef.child(imageFileName)
        Log.d("encImg", mountainsRef.toString())
        mountainsRef.putFile(imageURI)
            .addOnCompleteListener {
                Log.d("encImg", it.toString())
                if (it.isSuccessful) {
                    mSuccessHandler(imageURI.toString())
                    // 파일 업로드에 성공했기 때문에 파일을 다시 받아 오도록 해야함
//                    fbStorage.reference.child("images").child(imageFileName).downloadUrl
//                        .addOnSuccessListener { uri ->
//                            Log.d("encImg", "Upload Success: " + uri.toString())
//                            mSuccessHandler(uri.toString())
//                        }.addOnFailureListener {
//                            Log.d("encImg", "Upload Failed: ${it.toString()}")
//                            mErrorHandler()
//                        }
                } else {
                    mErrorHandler()
                }
            }
    }

    // 파일명으로 Firebase에서 이미지 다운로드
    private fun downloadImage(filename: String) {
        // storage 인스턴스 생성
        val storage = Firebase.storage
        // storage 참조
        val storageRef = storage.getReference("image")
        // storage에서 가져올 파일명 선언
        val mountainsRef = storageRef.child("${filename}.png")

        val downloadTask = mountainsRef.downloadUrl
        downloadTask.addOnSuccessListener { uri ->
            // 파일 다운로드 성공
            // Glide를 사용하여 이미지를 ImageView에 직접 가져오기
//            Glide.with(mainActivity).load(uri).into(binding.imageArea)
        }.addOnFailureListener {
            // 파일 다운로드 실패
        }
    }

    // 서버로부터 그룹키 가져오기
    private fun getGroupKey(userId: String) {
//        val retrofitInterface = RetrofitInterface.create(GlobalVari.getUrl())   //10.0.2.2
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

    private fun saveImage(uri: String) {
        var imageData = ImageData(challId, userId, uri)
        retrofitInterface.saveImage(imageData).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    Log.d("encImg", "success saveImage ${response.body()}")
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.d("encImg", "fail saveImage ${t}")
            }
        })
    }


    companion object {
        var groupKey: String? = null
    }

}