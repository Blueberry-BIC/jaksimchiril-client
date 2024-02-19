package com.example.bicapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.core.view.marginStart
import com.example.bicapplication.databinding.ActivityMainBinding
import com.example.bicapplication.mychall.MychallFragment
import com.google.android.material.navigation.NavigationBarView

class MainActivity : AppCompatActivity(), NavigationBarView.OnItemSelectedListener {
    private lateinit var homeFragment: HomeFragment
    private lateinit var mychallFragment: MychallFragment
    private lateinit var mystackFragment: MystackFragment
    private lateinit var mypageFragment: MypageFragment

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomnav.setOnItemSelectedListener(this)

        homeFragment = HomeFragment.newInstance()
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_frame, homeFragment).commit()

        //로컬에 저장된 유저정보가 없으면 깃허브id 입력받고 로컬db, 몽고db에 저장
        githubDialog()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.home -> {
                homeFragment = HomeFragment.newInstance()
                val transaction = supportFragmentManager.beginTransaction()
                transaction.addToBackStack(null)
                transaction.replace(R.id.main_frame, homeFragment).commit()
            }
            R.id.chall -> {
                mychallFragment = MychallFragment.newInstance()
                val transaction = supportFragmentManager.beginTransaction()
                transaction.addToBackStack(null)
                transaction.replace(R.id.main_frame, mychallFragment).commit()
            }
            R.id.stack -> {
                mystackFragment = MystackFragment.newInstance()
                val transaction = supportFragmentManager.beginTransaction()
                transaction.addToBackStack(null)
                transaction.replace(R.id.main_frame, mystackFragment).commit()
            }
            R.id.mypage -> {
                mypageFragment = MypageFragment.newInstance()
                val transaction = supportFragmentManager.beginTransaction()
                transaction.addToBackStack(null)
                transaction.replace(R.id.main_frame, mypageFragment).commit()
            }
        }

        return true
    }


    //깃허브id 입력받고 저장
    private fun githubDialog(){
        val builder = AlertDialog.Builder(this)
        builder.setMessage("본인 깃허브 ID를 입력해주세요.")
        builder.setCancelable(false) // 다이얼로그 화면 밖 터치 방지
        val editText = EditText(this)
        builder.setView(editText)

        builder.setPositiveButton(
            "완료"
        ) { dialog, which ->
        //로컬db와 몽고db에 저장해주는 로직 진행

        }
        builder.setNeutralButton(
            "다음에 하기"
        ) { dialog, which ->
        }

        builder.show() // 다이얼로그 보이기
    }
}