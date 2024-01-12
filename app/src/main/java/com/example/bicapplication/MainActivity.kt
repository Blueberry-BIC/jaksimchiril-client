package com.example.bicapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.example.bicapplication.databinding.ActivityMainBinding
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
}