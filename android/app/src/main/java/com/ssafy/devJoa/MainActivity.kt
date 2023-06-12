package com.ssafy.devJoa

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.activityViewModels
import com.ssafy.devJoa.config.ApplicationClass.Companion.sharedPreferencesUtil
import com.ssafy.devJoa.databinding.ActivityMainBinding
import com.ssafy.devJoa.page.PageActivity
import com.ssafy.devJoa.page.login.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

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
                    .setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in,R.anim.fade_out)
                    .replace(R.id.fragment_frame, LoginFragment())
                    .addToBackStack(null)
                    .commit()
            }
            "join" -> {
                supportFragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left,R.anim.exit_to_right)
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