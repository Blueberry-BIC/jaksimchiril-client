package com.example.bicapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.bicapplication.certify.CertifyStatusActivity
import com.example.bicapplication.databinding.ActivitySelectedchallBinding
import com.example.bicapplication.datamodel.ChallData

class SelectedchallActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySelectedchallBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectedchallBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initLayout()

    }

    private fun initLayout() {

        binding.apply {
            btnSelectedBack.setOnClickListener {
                finish()
            }
            btnChallParticipate.setOnClickListener {
                if (challData?.isProgress == 0) {
                    ParticipateActivity.challData = challData
                    val intent = Intent(this@SelectedchallActivity, ParticipateActivity::class.java)
                    startActivity(intent)
                } else if (challData?.isProgress == 1) {
                    val intent = Intent(this@SelectedchallActivity, CertifyStatusActivity::class.java)
                    startActivity(intent)
                }
            }

            if (challData?.isProgress == 0) {
                btnChallParticipate.text = "참가하기"
            } else if (challData?.isProgress == 1) {
                btnChallParticipate.text = "인증하기"
            }

            when (challData?.authMethod) {
                1 -> textAuthMethod.text = "이미지 인증"
                2 -> textAuthMethod.text = "깃허브 인증"
                3 -> textAuthMethod.text = "액션 인증"
            }
            when (challData?.isPublic) {
                true -> textIsPublic.text = "공개"
                false -> textIsPublic.text = "비공개"
                else -> {}
            }
            textChallName.text = challData?.challName
            textTotaldays.text = challData?.totalDays.toString()
            textviewFinishedDesc.text = challData?.challDesc
            textCategory.text = challData?.category
            textPeriod.text = challData?.enddate
            textMoney.text = challData?.money.toString()
            textUserNum.text = challData?.userNum.toString()
        }
    }

    companion object {
        var challData: ChallData? = null
    }

}