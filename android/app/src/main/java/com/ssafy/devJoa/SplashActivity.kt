package com.ssafy.devJoa

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.ImageView
import com.airbnb.lottie.LottieAnimationView
import com.ssafy.devJoa.databinding.ActivitySplashBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {
    lateinit var binding:ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        val images = listOf(R.id.icon1, R.id.icon2, R.id.icon3, R.id.icon4)
//        CoroutineScope(Dispatchers.Main).launch {
//            delay(1_200)
//            startActivity(Intent(this@SplashActivity, MainActivity::class.java).apply {
//                flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
//            })
//        }
//
//        CoroutineScope(Dispatchers.Main).launch {
//            for (i in 0..3) {
//                findViewById<ImageView>(images[i]).visibility = View.VISIBLE
//                for (j in 0 .. 3) {
//                    if (j == i)
//                        continue;
//                    findViewById<ImageView>(images[j]).visibility = View.GONE
//                }
//                delay(300)
//            }
//        }

        binding.lottieAnimation.speed = 1.5f
        CoroutineScope(Dispatchers.Main).launch {
            delay(2_000)
            startActivity(Intent(this@SplashActivity, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            })
        }
    }
}