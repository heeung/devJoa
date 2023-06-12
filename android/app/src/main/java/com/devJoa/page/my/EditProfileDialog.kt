package com.devJoa.page.my

import android.Manifest
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.devJoa.config.ApplicationClass
import com.devJoa.databinding.DialogProfileEditBinding
import com.devJoa.dto.User
import com.devJoa.page.PageActivity
import com.devJoa.util.RetrofitUtil
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okio.BufferedSink
import okio.source
import java.io.IOException

class EditProfileDialog(user: User, val mActivity: Context, val mFragment: MyPageFragment, val isMe: Boolean) : DialogFragment() {

    private var _binding: DialogProfileEditBinding? = null
    private val binding get() = _binding!!


    private var user = user
    var editProfileListener: EditProfileListener? = null

    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    private var url = ApplicationClass.SERVER_URL + "${user.imageUrl}"

    private lateinit var ProfileUri: Uri
    private lateinit var file: InputStreamRequestBody

    private lateinit var filePart: MultipartBody.Part

    override fun onAttach(context: Context) {
        super.onAttach(context)

        try {
            editProfileListener = context as EditProfileListener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement EditProfileDialog")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DialogProfileEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (isMe == true) {
            binding.modifyBtn.visibility = View.GONE
        } else {
            (mActivity as PageActivity).showToast("사진을 클릭해서 바꿔보세요!")
        }
        init()
    }

    fun init() {
        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == AppCompatActivity.RESULT_OK) {
                    result.data?.data.let { uri ->
                        if (uri == null) {
                            return@registerForActivityResult
                        }

                        ProfileUri = uri
                        file = InputStreamRequestBody(
                            "application/octet-stream".toMediaTypeOrNull()!!,
                            mActivity.contentResolver,
                            ProfileUri
                        )
                        filePart = MultipartBody.Part.createFormData("file", "file", file)

                        Glide.with(mActivity)
                            .load(Uri.parse(ProfileUri.toString())).skipMemoryCache(true)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .apply {
                                into(binding.profileImage)
                            }
                    }
                }
            }

        if (Build.VERSION_CODES.P <= Build.VERSION.SDK_INT) {
//            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
//            intent.addCategory(Intent.CATEGORY_OPENABLE)
//            intent.type = "image/*"
//            intent.putExtra("crop", true)
//            resultLauncher.launch(intent)
        } else {
            TedPermission.create()
                .setPermissionListener(object : PermissionListener {
                    override fun onPermissionGranted() {
//                        val intent = Intent(Intent.ACTION_PICK)
//                        intent.type = "image/*"
//                        intent.putExtra("crop", true)
//                        resultLauncher.launch(intent)
                    }

                    override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                        Toast.makeText(context, "프로필 사진 변경을 위해 사진 접근 권한이 필요합니다", Toast.LENGTH_SHORT)
                            .show()
                    }
                })
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE)
                .setDeniedMessage("프로필 사진 변경을 위해 사진 접근 권한이 필요합니다")
                .check()
        }



        Glide.with(this)
            .load(Uri.parse(url)).skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .apply {
                into(binding.profileImage)
            }

        binding.profileImage.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                if (isMe == false) {
                    val intent = Intent(Intent.ACTION_GET_CONTENT)
                    intent.setType("image/*")
                    resultLauncher.launch(intent)
                }
            }
        })

        binding.modifyBtn.setOnClickListener {

            editProfileListener?.onModifyProfileClicked()
            (mActivity as PageActivity).showToast("성공적으로 수정되었습니다.")
            CoroutineScope(Dispatchers.IO).launch {
                RetrofitUtil.fileService.uploadProfile(filePart,user.id.toLong())

                withContext(Dispatchers.Main) {
                    Glide.with(mActivity)
                        .load(Uri.parse(url)).skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .apply {
                            into((mFragment).binding.profileImage)
                            into((mActivity as PageActivity).binding.drawerNav.drawerProfileImage)
                            into((mActivity as PageActivity).binding.profileImage)
                        }
                    (mActivity as PageActivity).binding.drawerNav.drawerProfileImage
                }
            }

            dismiss()
        }

        binding.cancelButton.setOnClickListener {
            dismiss()
        }
    }


    interface EditProfileListener {
        fun onModifyProfileClicked()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setDimAmount(0.3f)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }
}

// 멀티파트 2개 만들 때 사용된 InputStreamRequestBody 클래스

class InputStreamRequestBody(contentType: MediaType, contentResolver: ContentResolver, uri: Uri?) :
    RequestBody() {
    private val contentType: MediaType
    private val contentResolver: ContentResolver
    private val uri: Uri

    init {
        if (uri == null) throw NullPointerException("uri == null")
        this.contentType = contentType
        this.contentResolver = contentResolver
        this.uri = uri
    }

    override fun contentType(): MediaType {
        return contentType
    }

    @Throws(IOException::class)
    override fun contentLength(): Long {
        return -1
    }

    @Throws(IOException::class)
    override fun writeTo(sink: BufferedSink) {
        sink.writeAll(contentResolver.openInputStream(uri)!!.source())
    }
}