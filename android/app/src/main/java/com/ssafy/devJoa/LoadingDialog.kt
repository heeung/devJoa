package com.ssafy.devJoa

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Window
import com.bumptech.glide.Glide.init
import com.ssafy.devJoa.databinding.DialogLoadingBinding

class LoadingDialog(context: Context): Dialog(context) {
    lateinit var binding: DialogLoadingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogLoadingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 배경 투명하게
//        window!!.setBackgroundDrawable(ColorDrawable())
        initProgress()
    }

    private fun initProgress() {
        window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
        binding.progressBar.setBackgroundResource(R.drawable.loading_image)
        val animationDrawable = binding.progressBar.background as AnimationDrawable
        animationDrawable.start()
    }
}