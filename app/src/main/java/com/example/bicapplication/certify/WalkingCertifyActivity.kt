package com.example.bicapplication.certify

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.bicapplication.databinding.ActivityWalkingCertifyBinding

//실외운동 인증화면 -> 발걸음 감지 센서로 만보계 기능 수행하는 액티비티
class WalkingCertifyActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var binding: ActivityWalkingCertifyBinding

    var minStep = 5000; //유저가 넘겨야할 최소 기준 걸음수

    // getSystemService() 메소드에 SENSOR_SERVICE 상수를 전달하여 SensorManager클래스의 인스턴스를 만듬.
    var sensorManager: SensorManager? = null
    var stepDetectorSensor: Sensor? = null
    var currentSteps =0 //현재 걸음수

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWalkingCertifyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        stepDetectorSensor = sensorManager!!.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)

        //발걸음 감지 내장 센서 권한 묻기
        if(stepDetectorSensor == null){
            Toast.makeText(this, "센서 기능을 지원하지 않는 폰입니다.", Toast.LENGTH_SHORT).show()
        }else{
            //권한이 허용되어 있지 않다면
            if(ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED){
                //센서기능 권한요청
                requestPermissionLauncher.launch(arrayOf(Manifest.permission.ACTIVITY_RECOGNITION))
            }else{ //권한있는 경우
            }
        }

        //다시시작 버튼 클릭시
        binding.resetButton.setOnClickListener {
            currentSteps = 0
            binding.stepCountView.text = currentSteps.toString()
        }

        //인증완료 버튼 클릭시 - CertfiyStatus액티비티로 인증 결과 보내주기
        binding.finishButton.setOnClickListener {
            val stepCount = binding.stepCountView.text.toString().toInt()  //유저의 걸음수
            if(stepCount >= minStep){  //성공시
                moveActivity(true)
            }else{  //실패시
                moveActivity(false)
            }

        }

    }

    //센서기능 권한요청을 위한 변수
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()){ it ->
        //Log.e("태그", "requestPermissionLauncher: 건수 : ${it.size}")
        var results = true
        it.values.forEach{
            if(!it) {
                results = false
                return@forEach
            }
        }
        if(!results){
            Toast.makeText(this, "권한이 필요합니다.", Toast.LENGTH_SHORT).show()
        }else{  //모두 권한이 있을 경우

        }
    }

    //만보계 기능
    override fun onResume() {
        super.onResume()
        //Log.e("태그","만보게 onResume")
        sensorManager?.registerListener(this, stepDetectorSensor, SensorManager.SENSOR_DELAY_FASTEST)

    }

    override fun onSensorChanged(event: SensorEvent) {
        // 걸음 센서 이벤트 발생시
        if (event.sensor?.type == Sensor.TYPE_STEP_DETECTOR) {
            // 센서 이벤트가 발생할때 마다 걸음수 증가
            currentSteps++
            binding.stepCountView.text = currentSteps.toString()
            //Log.e("태그","걸음 감지 성공")
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }


    //인증이 다 끝난후 성공여부 데이터를 인증현황 액티비티로 전달 및 이동을 위한 메소드
    private fun moveActivity(success:Boolean,){
        val intent = Intent(this@WalkingCertifyActivity, CertifyStatusActivity::class.java)
        intent.putExtra("성공여부", success)
        intent.putExtra("걸음수인증방문", true)
        setResult(5,intent)
        finish()
    }


}