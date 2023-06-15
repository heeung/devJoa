package com.devJoa.page.login

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.devJoa.R
import com.devJoa.config.ApplicationClass
import com.devJoa.databinding.FragmentLoginBinding
import com.devJoa.page.PageActivity

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val loginViewModel: LoginViewModel by activityViewModels()

    lateinit var mActivity: com.devJoa.MainActivity

    private var isLoginTextFilled = false
    private var isPasswordTextFilled = false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as com.devJoa.MainActivity
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        return binding.root
    }

    // 화면 구성 코드
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        registerObserver()
        resisterEditTextChangeListener()

        binding.loginBtn.apply {
            isEnabled = false
            setBackgroundResource(R.drawable.join_radius_button)
            setOnClickListener {
                val userId = binding.IDEdit.text.toString()
                val password = binding.passwordEdit.text.toString()
                loginViewModel.login(userId, password)
            }
        }

        binding.backBtn.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun registerObserver() {
        loginViewModel.user.observe(viewLifecycleOwner) {
            if (it.userId.isNotEmpty()) { //성공
                mActivity.showToast("로그인 되었습니다.")
                // sharedpreference에 저장
                ApplicationClass.sharedPreferencesUtil.addUser(it)
                startActivity(Intent(mActivity, PageActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                })
            } else { //실패
                mActivity.showToast("id 혹은 password를 확인해주세요.")
            }
        }
    }

    private fun resisterEditTextChangeListener() {
        binding.IDEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(str: CharSequence?, p1: Int, p2: Int, p3: Int) {
                when(str?.isEmpty()) {
                     true -> {
                        isLoginTextFilled = false
                         changeButtonToggle()
                    }
                    else -> {
                        isLoginTextFilled = true
                        changeButtonToggle()
                    }
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })

        binding.passwordEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(str: CharSequence?, p1: Int, p2: Int, p3: Int) {
                when(str?.isEmpty()) {
                    true -> {
                        isPasswordTextFilled = false
                        changeButtonToggle()
                    }
                    else -> {
                        isPasswordTextFilled = true
                        changeButtonToggle()
                    }
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })
    }

    private fun changeButtonToggle() {
        if (isLoginTextFilled && isPasswordTextFilled) {
            binding.loginBtn.setBackgroundResource(R.drawable.login_radius_button)
            binding.loginBtn.isEnabled = true
        } else {
            binding.loginBtn.setBackgroundResource(R.drawable.join_radius_button)
            binding.loginBtn.isEnabled = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}