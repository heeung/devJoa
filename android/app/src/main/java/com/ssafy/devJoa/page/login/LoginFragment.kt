package com.ssafy.devJoa.page.login

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.text.InputType
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.ssafy.devJoa.MainActivity
import com.ssafy.devJoa.R
import com.ssafy.devJoa.api.UserService
import com.ssafy.devJoa.config.ApplicationClass
import com.ssafy.devJoa.databinding.FragmentLoginBinding
import com.ssafy.devJoa.page.PageActivity
import com.ssafy.devJoa.page.PageViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val loginViewModel: LoginViewModel by activityViewModels()

    lateinit var mActivity: MainActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as MainActivity
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

//        // 화면 다른 곳 클릭시 포커스 빼기 대응
//        binding.root.setOnTouchListener { _, event ->
//            if (event.action == MotionEvent.ACTION_DOWN) {
//                val currentFocus = activity?.currentFocus
//                if (currentFocus is EditText) {
//                    val outRect = Rect()
//                    currentFocus.getGlobalVisibleRect(outRect)
//                    if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
//                        currentFocus.clearFocus()
//                        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//                        imm.hideSoftInputFromWindow(currentFocus.windowToken, 0)
//                    }
//                }
//            }
//            false
//        }

        return binding.root
    }

    // 화면 구성 코드
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        changeTitleColor()
        registerObserver()

        binding.loginBtn.setOnClickListener {
            val userId = binding.IDEdit.text.toString()
            val password = binding.passwordEdit.text.toString()
            loginViewModel.login(userId, password)
        }

        binding.joinBtn.setOnClickListener {
            mActivity.changeFragment("join")
        }
    }

    fun registerObserver() {
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun changeTitleColor() {
        val spannableStringBuilder = SpannableStringBuilder("개발자들이\n조아하는\n아이템")
        spannableStringBuilder.apply{
            val color = ContextCompat.getColor(mActivity, R.color.butter_color_more)
            setSpan(ForegroundColorSpan(color), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            setSpan(ForegroundColorSpan(color), 6, 7, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            setSpan(ForegroundColorSpan(color), 11, 12, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            binding.titleTv2.text = spannableStringBuilder
        }
    }
}