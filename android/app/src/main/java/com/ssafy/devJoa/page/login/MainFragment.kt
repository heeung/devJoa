package com.ssafy.devJoa.page.login

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.ssafy.devJoa.MainActivity
import com.ssafy.devJoa.R
import com.ssafy.devJoa.databinding.FragmentLoginBinding
import com.ssafy.devJoa.databinding.FragmentMainBinding

class MainFragment : Fragment() {
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    lateinit var mActivity: MainActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    // 화면 구성 코드
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        changeTitleColor()

        binding.loginBtn.setOnClickListener {
            mActivity.changeFragment("login")
        }

        binding.joinBtn.setOnClickListener {
            mActivity.changeFragment("join")
        }
    }

    private fun changeTitleColor() {
        val spannableStringBuilder = SpannableStringBuilder("개발자들이\n조아하는\n아이템")
        spannableStringBuilder.apply{
            val color = ContextCompat.getColor(mActivity, R.color.butter_color_more)
            setSpan(ForegroundColorSpan(color), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            setSpan(ForegroundColorSpan(color), 6, 7, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            setSpan(ForegroundColorSpan(color), 11, 12, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            binding.titleTv.text = spannableStringBuilder
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
