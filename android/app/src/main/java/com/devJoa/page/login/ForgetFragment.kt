package com.devJoa.page.login

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.devJoa.databinding.FragmentForgetBinding

class ForgetFragment : Fragment() {
    private var _binding: FragmentForgetBinding? = null
    private val binding get() = _binding!!

    lateinit var mActivity: com.devJoa.MainActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as com.devJoa.MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentForgetBinding.inflate(inflater, container, false)
        return binding.root
    }

    // 화면 구성 코드
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Glide로 이미지 표시하기
        val url = "http://192.168.33.116:8080/images/aa"
        Glide.with(this)
            .load(Uri.parse(url))
            .into(binding.imageView)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}