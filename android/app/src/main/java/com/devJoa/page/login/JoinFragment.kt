package com.devJoa.page.login

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.devJoa.R
import com.devJoa.databinding.FragmentJoinBinding
import com.devJoa.dto.User
import com.devJoa.util.RetrofitUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class JoinFragment : Fragment() {
    
    private var _binding: FragmentJoinBinding? = null
    private val binding get() = _binding!!

    private var jobGroup = "MOBILE"
    lateinit var mActivity: com.devJoa.MainActivity
    private var isIdChecked = false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as com.devJoa.MainActivity
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentJoinBinding.inflate(inflater, container, false)

        // 화면 다른 곳 클릭시 포커스 빼기 대응
        binding.root.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                val currentFocus = activity?.currentFocus
                if (currentFocus is EditText) {
                    val outRect = Rect()
                    currentFocus.getGlobalVisibleRect(outRect)
                    if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                        currentFocus.clearFocus()
                        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(currentFocus.windowToken, 0)
                    }
                }
            }
            false
        }

        return binding.root
    }

    // 화면 구성 코드
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        settingRadioBtn()
        checkValid()
        checkIDValid()

        binding.joinBtn.setOnClickListener {
            if (isIdChecked == false) {
                mActivity.showToast("아이디 중복확인 버튼을 눌러주세요.")
            } else if (binding.passwordEditTextfield.text.toString()
                != binding.passwordCheckEditTextfield.text.toString()) { // 비밀번호 두개 다를 때
                mActivity.showToast("비밀번호 두개가 다릅니다.")
                binding.passwordEditTextfield.requestFocus()
                binding.passwordCheckTextfield.error = "Invalid input"
            } else if (binding.passwordEditTextfield.text.toString().isEmpty()) { // 비밀번호 칸 비었을 때
                mActivity.showToast("비밀번호칸이 비어있습니다.")
                binding.passwordEditTextfield.requestFocus()
                binding.passwordTextfield.error = "Invalid input"
            } else if (binding.editNickName.text.toString().isEmpty()) { // 닉네임 비었을 때
                mActivity.showToast("닉네임칸이 비었습니다.")
                binding.editNickName.requestFocus()
                binding.nickName.error = "Invalid input"
            } else {
                mActivity.showToast("회원가입을 축하드립니다.")
                val userId = binding.IDEditTextfield.text.toString()
                val pass = binding.passwordEditTextfield.text.toString()
                val nick = binding.editNickName.text.toString()

                CoroutineScope(Dispatchers.IO).launch {
                    RetrofitUtil.userService.inserUser(User(userId, pass, nick, jobGroup))
                    withContext(Dispatchers.Main) {
                        mActivity.changeFragment("main")
                    }
                }
            }
        }
    }

    fun checkIDValid() {
        binding.idCheckBtn.setOnClickListener {
            if (binding.IDEditTextfield.text.toString().isEmpty()) { //아이디 칸 비었을 떄
                binding.IDTextfield.error = "Invalid input"
                mActivity.showToast("아이디 입력칸이 비었습니다.")
                isIdChecked = false
            } else {
                val userId = binding.IDEditTextfield.text.toString()
                CoroutineScope(Dispatchers.IO).launch {
                    val isUsed = RetrofitUtil.userService.isUsedId(userId)
                    if (isUsed) {
                        withContext(Dispatchers.Main) {
                            binding.IDTextfield.error = "Invalid input"
                            mActivity.showToast("사용중인 아이디 입니다.")
                            isIdChecked = false
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            binding.IDTextfield.setEndIconDrawable(R.drawable.baseline_check_box_24)
                            binding.IDTextfield.setEndIconTintList(ColorStateList.valueOf(
                                ContextCompat.getColor(mActivity, R.color.sky_blue_color)))
                            binding.IDTextfield.isEndIconVisible = true
                            mActivity.showToast("사용 가능한 아이디입니다.")
                            isIdChecked = true
                        }
                    }
                }
            }
        }
    }

    fun checkValid(): Boolean {
        binding.IDEditTextfield.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (s != null && s.length > 15) {
                    binding.IDTextfield.error = "Invalid input"
                } else {
                    binding.IDTextfield.error = null
                }
            }
        })
        binding.passwordEditTextfield.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (s != null && s.length > 15) {
                    binding.passwordTextfield.error = "Invalid input"
                } else {
                    binding.passwordTextfield.error = null
                }
            }
        })
        binding.passwordCheckEditTextfield.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (s != null && s.length > 15) {
                    binding.passwordCheckTextfield.error = "Invalid input"
                } else {
                    binding.passwordCheckTextfield.error = null
                }
            }
        })
        binding.editNickName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (s != null && s.length > 15) {
                    binding.nickName.error = "Invalid input"
                } else {
                    binding.nickName.error = null
                }
            }
        })
        return true
    }

    fun settingRadioBtn() {
        binding.radioBtn1.setOnClickListener {
            binding.radioBtn1.isChecked = true
            binding.radioBtn2.isChecked = false
            binding.radioBtn3.isChecked = false
            binding.radioBtn4.isChecked = false
            jobGroup = "MOBILE"
        }

        binding.radioBtn2.setOnClickListener {
            binding.radioBtn1.isChecked = false
            binding.radioBtn2.isChecked = true
            binding.radioBtn3.isChecked = false
            binding.radioBtn4.isChecked = false
            jobGroup = "BACKEND"
        }

        binding.radioBtn3.setOnClickListener {
            binding.radioBtn1.isChecked = false
            binding.radioBtn2.isChecked = false
            binding.radioBtn3.isChecked = true
            binding.radioBtn4.isChecked = false
            jobGroup = "FRONTEND"
        }
        binding.radioBtn4.setOnClickListener {
            binding.radioBtn1.isChecked = false
            binding.radioBtn2.isChecked = false
            binding.radioBtn3.isChecked = false
            binding.radioBtn4.isChecked = true
            jobGroup = "ETC"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}