package com.ssafy.devJoa.login

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ssafy.devJoa.MainActivity
import com.ssafy.devJoa.R
import com.ssafy.devJoa.databinding.FragmentJoinBinding
import com.ssafy.devJoa.databinding.FragmentLoginBinding

class JoinFragment : Fragment() {
    
    private var _binding: FragmentJoinBinding? = null
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
        _binding = FragmentJoinBinding.inflate(inflater, container, false)
        return binding.root
    }

    // 화면 구성 코드
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}