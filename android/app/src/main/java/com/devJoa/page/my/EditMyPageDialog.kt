package com.devJoa.page.my

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.devJoa.R
import com.devJoa.databinding.EditMypageDialogBinding
import com.devJoa.dto.User
import com.devJoa.page.PageActivity

private const val TAG = "EditMyPageDialog_싸피"
class EditMyPageDialog(user: User, val mActivity: Context) : DialogFragment(){

    private var _binding: EditMypageDialogBinding? = null
    private val binding get() = _binding!!

    private var user = user
    var editMyPageDialogListener: EditMyPageDialogListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)

        try {
            editMyPageDialogListener = context as EditMyPageDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement EditMyPageDialogListener")
        }
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireContext(), R.style.BottomSheetDialogTheme)
        dialog.setContentView(R.layout.edit_mypage_dialog)
        return dialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = EditMypageDialogBinding.inflate(inflater, container, false)
        // Bottom Sheet Dialog의 뷰와 동작 정의

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
        settingRadioBtn()
        settingLevelRadioBtn()
        checkValid()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setDimAmount(0.2f)
        dialog?.window?.setLayout(1000, 1800)
    }

    fun init() {
        binding.idEditTextfield.setText(user.userId)
        binding.idEditTextfield.keyListener = null
        binding.nicknameEditTextfield.setText(user.nickname)
        binding.githubIdEditTextfield.setText(user.githubId)

        binding.modifyBtn.setOnClickListener {
            if (binding.passwordEditTextfield.text.toString()
                != binding.passwordEditCheckTextfield.text.toString()) { // 비밀번호 두개 다를 때
                binding.passwordCheckTextfield.error = "Invalid input"
                (mActivity as PageActivity).showToast("비밀번호가 같은지 확인해주세요.")
                binding.passwordEditTextfield.requestFocus()
            } else if (binding.passwordEditTextfield.text.toString().isEmpty()) { // 비밀번호 칸 비었을 때
                binding.passwordTextfield.error = "Invalid input"
                (mActivity as PageActivity).showToast("비밀번호 칸이 비었습니다")
                binding.passwordEditTextfield.requestFocus()
            } else if (binding.nicknameEditTextfield.text.toString().isEmpty()) { // 닉네임 비었을 때
                (mActivity as PageActivity).showToast("닉네임 칸이 비었습니다")
                binding.nicknameTextfield.error = "Invalid input"
                binding.nicknameEditTextfield.requestFocus()
            } else {
                user.nickname = binding.nicknameEditTextfield.text.toString()
                user.githubId = binding.githubIdEditTextfield.text.toString()
                user.password = binding.passwordEditTextfield.text.toString()
                editMyPageDialogListener?.onModifyClicked(user)
                (mActivity as PageActivity).showToast("성공적으로 수정되었습니다.")
                dismiss()
            }
        }

        binding.cancelButton.setOnClickListener {
            dismiss()
        }

        when (user.jobtype) {
            "MOBILE" -> {
                binding.radioBtn1.isChecked = true
                binding.radioBtn2.isChecked = false
                binding.radioBtn3.isChecked = false
                binding.radioBtn4.isChecked = false
            }
            "BACKEND" -> {
                binding.radioBtn1.isChecked = false
                binding.radioBtn2.isChecked = true
                binding.radioBtn3.isChecked = false
                binding.radioBtn4.isChecked = false
            }
            "FRONTEND" -> {
                binding.radioBtn1.isChecked = false
                binding.radioBtn2.isChecked = false
                binding.radioBtn3.isChecked = true
                binding.radioBtn4.isChecked = false
            }
            "ETC" -> {
                binding.radioBtn1.isChecked = false
                binding.radioBtn2.isChecked = false
                binding.radioBtn3.isChecked = false
                binding.radioBtn4.isChecked = true
            }
        }
        when (user.careerLevel) {
            "JUNIOR" -> {
                binding.levelRadioBtn1.isChecked = true
                binding.levelRadioBtn2.isChecked = false
                binding.levelRadioBtn3.isChecked = false
                binding.levelRadioBtn4.isChecked = false
            }
            "MID_LEVEL" -> {
                binding.levelRadioBtn1.isChecked = false
                binding.levelRadioBtn2.isChecked = true
                binding.levelRadioBtn3.isChecked = false
                binding.levelRadioBtn4.isChecked = false
            }
            "SENIOR" -> {
                binding.levelRadioBtn1.isChecked = false
                binding.levelRadioBtn2.isChecked = false
                binding.levelRadioBtn3.isChecked = true
                binding.levelRadioBtn4.isChecked = false
            }
            "NONE" -> {
                binding.levelRadioBtn1.isChecked = false
                binding.levelRadioBtn2.isChecked = false
                binding.levelRadioBtn3.isChecked = false
                binding.levelRadioBtn4.isChecked = true
            }

        }
    }

    fun settingRadioBtn() {
        binding.radioBtn1.setOnClickListener {
            binding.radioBtn1.isChecked = true
            binding.radioBtn2.isChecked = false
            binding.radioBtn3.isChecked = false
            binding.radioBtn4.isChecked = false
            user.jobtype = "MOBILE"
        }

        binding.radioBtn2.setOnClickListener {
            binding.radioBtn1.isChecked = false
            binding.radioBtn2.isChecked = true
            binding.radioBtn3.isChecked = false
            binding.radioBtn4.isChecked = false
            user.jobtype = "BACKEND"
        }

        binding.radioBtn3.setOnClickListener {
            binding.radioBtn1.isChecked = false
            binding.radioBtn2.isChecked = false
            binding.radioBtn3.isChecked = true
            binding.radioBtn4.isChecked = false
            user.jobtype = "FRONTEND"
        }
        binding.radioBtn4.setOnClickListener {
            binding.radioBtn1.isChecked = false
            binding.radioBtn2.isChecked = false
            binding.radioBtn3.isChecked = false
            binding.radioBtn4.isChecked = true
            user.jobtype = "ETC"
        }
    }

    fun settingLevelRadioBtn() {
        binding.levelRadioBtn1.setOnClickListener {
            binding.levelRadioBtn1.isChecked = true
            binding.levelRadioBtn2.isChecked = false
            binding.levelRadioBtn3.isChecked = false
            binding.levelRadioBtn4.isChecked = false
            user.careerLevel = "JUNIOR"
        }

        binding.levelRadioBtn2.setOnClickListener {
            binding.levelRadioBtn1.isChecked = false
            binding.levelRadioBtn2.isChecked = true
            binding.levelRadioBtn3.isChecked = false
            binding.levelRadioBtn4.isChecked = false
            user.careerLevel = "MID_LEVEL"
        }

        binding.levelRadioBtn3.setOnClickListener {
            binding.levelRadioBtn1.isChecked = false
            binding.levelRadioBtn2.isChecked = false
            binding.levelRadioBtn3.isChecked = true
            binding.levelRadioBtn4.isChecked = false
            user.careerLevel = "SENIOR"
        }
        binding.levelRadioBtn4.setOnClickListener {
            binding.levelRadioBtn1.isChecked = false
            binding.levelRadioBtn2.isChecked = false
            binding.levelRadioBtn3.isChecked = false
            binding.levelRadioBtn4.isChecked = true
            user.careerLevel = "NONE"
        }
    }

    fun checkValid() {
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
        binding.nicknameEditTextfield.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (s != null && s.length > 15) {
                    binding.nicknameTextfield.error = "Invalid input"
                } else {
                    binding.nicknameTextfield.error = null
                }
            }
        })
    }

    interface EditMyPageDialogListener {
        fun onModifyClicked(user: User)
    }

}