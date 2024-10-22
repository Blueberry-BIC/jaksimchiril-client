package com.example.bicapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.bicapplication.databinding.ActivityNewchallBinding
import android.widget.ArrayAdapter
import com.example.bicapplication.datamodel.ChallData
import com.example.bicapplication.klaytn.PrepareRespData
import com.example.bicapplication.retrofit.RetrofitInterface
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.concurrent.thread
import androidx.core.util.Pair
import com.example.bicapplication.retrofit.ChallDBManager
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Calendar

class NewchallActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewchallBinding
    var challdata = ChallData.getDefault()
    val retrofitInterface = RetrofitInterface.create(GlobalVari.getUrl())
    val challDbManager = ChallDBManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewchallBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initLayout()

//        val todayDate: LocalDate = LocalDate.now()
//        Log.d("DATE", todayDate.toString())

    }

    private fun initLayout() {

        //dropdown menu
        val categoryItems = resources.getStringArray(R.array.category_items)
        val arrayAdapter = ArrayAdapter(this, R.layout.dropdown_category_item, categoryItems)
        binding.autocomepletetextviewNewchallCategory.setAdapter(arrayAdapter)

        binding.apply {
            //취소하기 버튼 -> MainActivity로 이동
            btnNewchallExit.setOnClickListener {
                val intent = Intent(this@NewchallActivity, MainActivity::class.java)
                startActivity(intent)
            }
            // 챌린지 공개여부 선택
            btnChooseOpen.setOnCheckedChangeListener { _, checkedid ->
                when (checkedid) {
                    true -> {
                        challdata.isPublic = true
                    }
                    false -> {
                        challdata.isPublic = false
                    }
                }
            }
            // 챌린지 인증방식 선택
//            radiogroupNewchallAuthMethod.setOnCheckedChangeListener { _, checkedid ->
//                when (checkedid) {
//                    R.id.radiobtn_newchall_image -> {
//                        challdata.authMethod = 1
//                    }
//                    R.id.radiobtn_newchall_github -> {
//                        challdata.authMethod = 2
//                    }
//                    R.id.radiobtn_newchall_action -> {
//                        challdata.authMethod = 3
//                    }
//                }
//            }

            // 신체단련 카테고리에서 챌린지 종목 선택
            radiogroupNewchallExercise.setOnCheckedChangeListener { _, checkedid ->
                when (checkedid) {
                    R.id.radiobtn_newchall_indoor -> challdata.authMethod = 1
                    R.id.radiobtn_newchall_outdoor -> challdata.authMethod = 4
                }
            }

            // 챌린지 카테고리 선택
            autocomepletetextviewNewchallCategory.setOnItemClickListener { parent, view, position, id ->
                var item = autocomepletetextviewNewchallCategory.text.toString()
                challdata.category = item

                if (item == "신체 단련") {
                    exerciseText.visibility = View.VISIBLE
                    radiogroupNewchallExercise.visibility = View.VISIBLE
                } else {
                    exerciseText.visibility = View.GONE
                    radiogroupNewchallExercise.visibility = View.GONE
                }

                when(item) {
                    "코딩 스킬 향상" -> challdata.authMethod = 2
                    "시사/교양" -> challdata.authMethod = 5
                    "생활" -> challdata.authMethod = 3
                }

            }
            // 챌린지 기간 선택
            imgbtnNewchallDaterange.setOnClickListener {
                val dateRangePicker = MaterialDatePicker.Builder.dateRangePicker()
                    .setTitleText("챌린지 기간을 선택해주세요")
                    .build()
                dateRangePicker.show(supportFragmentManager, "data_picker")
                dateRangePicker.addOnPositiveButtonClickListener(object :
                    MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>> {
                    override fun onPositiveButtonClick(selection: Pair<Long, Long>?) {
                        val calendar = Calendar.getInstance()
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd")

                        calendar.timeInMillis = selection?.first ?: 0
                        challdata.startdate = dateFormat.format(calendar.time).toString()
                        val start = dateFormat.parse(challdata.startdate).time

                        calendar.timeInMillis = selection?.second ?: 0
                        challdata.enddate = dateFormat.format(calendar.time).toString()
                        val end = dateFormat.parse(challdata.enddate).time

                        challdata.totalDays = (end - start) / (24 * 60 * 60 * 1000) + 1
                        textviewNewchallDaterange.text = dateRangePicker.headerText

                        val todayDate: LocalDate = LocalDate.now()

                    }
                })
            }

            // 개설하기 버튼
            btnNewchallCreate.setOnClickListener {
                challdata.challName = edittextNewchallName.text.toString()
                challdata.challDesc = editextmultiLineNewchallDesc.text.toString()
                challdata.money = edittextNewchallDeposit.text.toString().toInt()

                if (challdata.isPublic == true) {
                    challdata.passwd = 0
                } else {
                    challdata.passwd = edittextNewchallGrouppwd.text.toString().toInt()
                }

                // DB에 입력한 내용 저장
//                saveChallInfo()
                challDbManager.postChallInfo(challdata)

                val intent = Intent(this@NewchallActivity, MainActivity::class.java)
                startActivity(intent)

            }

        }
    }

    // DB에 챌린지 관련 정보 저장
    private fun saveChallInfo() {

        retrofitInterface.postChallInfo(challdata).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    Log.d("CHALL", "success saveChall1 ${response.body()}")
                }
                else {
                    Log.d("CHALL", "success saveChall2 ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.d("CHALL", "fail saveChall ${t}")
            }
        })
    }

}