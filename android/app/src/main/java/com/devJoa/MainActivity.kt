package com.devJoa

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.devJoa.config.ApplicationClass.Companion.sharedPreferencesUtil
import com.devJoa.databinding.ActivityMainBinding
import com.devJoa.page.PageActivity
import com.devJoa.page.login.ForgetFragment
import com.devJoa.page.login.JoinFragment
import com.devJoa.page.login.LoginFragment
import com.devJoa.page.login.MainFragment

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //로그인 된 상태인지 확인
        var user = sharedPreferencesUtil.getUser()

        //로그인 상태 확인. id가 있다면 로그인 된 상태
        if (user.userId != ""){
            startActivity(Intent(this, PageActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
        } else {
            // 가장 첫 화면은 홈 화면의 Fragment로 지정
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_frame, MainFragment())
                .commit()
        }
//        supportFragmentManager.beginTransaction()
//                .replace(R.id.fragment_frame, MainFragment())
//                .commit()
    }

    fun changeFragment(str: String) {
        when (str) {
            "main" -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_frame, MainFragment())
                    .commit()
            }
            "login" -> {
                supportFragmentManager.beginTransaction()
                    .setCustomAnimations(
                        R.anim.enter_from_right,
                        R.anim.exit_to_left,
                        R.anim.enter_from_left,
                        R.anim.exit_to_right
                    )
                    .replace(R.id.fragment_frame, LoginFragment())
                    .addToBackStack(null)
                    .commit()
            }
            "join" -> {
                supportFragmentManager.beginTransaction()
                    .setCustomAnimations(
                        R.anim.enter_from_right,
                        R.anim.exit_to_left,
                        R.anim.enter_from_left,
                        R.anim.exit_to_right
                    )
                    .replace(R.id.fragment_frame, JoinFragment())
                    .addToBackStack(null)
                    .commit()
            }
            "findId" -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_frame, LoginFragment())
                    .addToBackStack(null)
                    .commit()
            }
            "findPassword" -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_frame, LoginFragment())
                    .addToBackStack(null)
                    .commit()
            }
            "forget" -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_frame, ForgetFragment())
                    .addToBackStack(null)
                    .commit()
            }
        }
    }
    fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}