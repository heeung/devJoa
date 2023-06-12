package com.ssafy.devJoa.page.my

import android.app.AlertDialog
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.graphics.drawable.TransitionDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.ssafy.devJoa.MainActivity
import com.ssafy.devJoa.R
import com.ssafy.devJoa.config.ApplicationClass
import com.ssafy.devJoa.databinding.FragmentHomeBinding
import com.ssafy.devJoa.databinding.FragmentMyPageBinding
import com.ssafy.devJoa.dto.*
import com.ssafy.devJoa.page.PageActivity
import com.ssafy.devJoa.page.PageViewModel
import com.ssafy.devJoa.page.login.LoginViewModel
import com.ssafy.devJoa.page.post.PostAdapter
import com.ssafy.devJoa.util.RetrofitUtil
import com.ssafy.devJoa.util.SharedPreferencesUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "MyPageFragment_싸피"
class MyPageFragment : Fragment() {
    private var _binding: FragmentMyPageBinding? = null
    val binding get() = _binding!!
    private lateinit var adapter_keyboard: MyPageAdapter
    private lateinit var adapter_mouse: MyPageAdapter
    private lateinit var adapter_software: MyPageAdapter
    private lateinit var adapter_monitor: MyPageAdapter
    private lateinit var adapter_stand: MyPageAdapter
    private lateinit var likedPostList: List<LikedPost>
    private lateinit var myPageItemList: List<MyPage>
    private lateinit var adapter_mypage: MyPageAdapter

    private var isFollowing = false

    lateinit var mActivity: PageActivity
    private val pageViewModel: PageViewModel by activityViewModels()
    lateinit var user: User

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as PageActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    // 화면 구성 코드
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        user = pageViewModel.user.value!!

        mActivity.nowPageFABToggle()
        init()
    }

    private fun showBottomSheetDialog() {
        val editMyPageDialog = EditMyPageDialog(user, mActivity)
        editMyPageDialog.show(parentFragmentManager, "editMyPageDialog")
    }

    private fun init() {
        if (ApplicationClass.sharedPreferencesUtil.getUser().id == user.id) {
            binding.modifyBtn.visibility = View.VISIBLE
            binding.followToggle.visibility = View.INVISIBLE
            CoroutineScope(Dispatchers.IO).launch {
                val map = RetrofitUtil.userService.getFollowCnt(user.id)
                withContext(Dispatchers.Main) {
                    binding.followerTv.text = map.get("followerCount").toString()
                    binding.followingTv.text = map.get("followingCount").toString()
                }
            }
            binding.followerTv
        } else {
            binding.modifyBtn.visibility = View.GONE
            binding.followToggle.visibility = View.VISIBLE
        }

        binding.followToggle.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val myId = ApplicationClass.sharedPreferencesUtil.getUser().id
                val toId = user.id
                val userNew = RetrofitUtil.followService.followToggle(FollowToggleKey(myId, toId))
                withContext(Dispatchers.Main) {
                    Log.d(TAG, "init: ${userNew}")
                    pageViewModel.setUser(userNew)
                }
            }
        }

        binding.modifyBtn.setOnClickListener {
            showBottomSheetDialog()
        }
        pageViewModel.user.observe(viewLifecycleOwner) {
            binding.nickNameTv.text = it.nickname
            binding.jobGroupTv.text = it.jobtype
            binding.jobYearTv.text = it.careerLevel
            binding.followerTv.text = it.followerCount.toString()
            binding.followingTv.text = it.followingCount.toString()

            lifecycleScope.launch {
                isFollowing = RetrofitUtil.followService.isFollow(
                    FollowToggleKey(
                        ApplicationClass.sharedPreferencesUtil.getUser().id,
                        user.id
                    )
                )
                if (isFollowing) {
                    binding.followToggle.text = "unfollow"
//                    binding.myItemToggleButton.setBackgroundResource(R.drawable.login_radius_button_blue)
                    val oldBackground = binding.followToggle.background
                    val newBackground =
                        ContextCompat.getDrawable(mActivity, R.drawable.login_radius_button_blue)
                    val transitionDrawable =
                        TransitionDrawable(arrayOf(oldBackground, newBackground))
                    transitionDrawable.isCrossFadeEnabled = true
                    transitionDrawable.startTransition(300)
                    binding.followToggle.background = transitionDrawable
                } else {
                    binding.followToggle.text = "follow"
//                    binding.myItemToggleButton.setBackgroundResource(R.drawable.login_radius_button)
                    val oldBackground = binding.followToggle.background
                    val newBackground =
                        ContextCompat.getDrawable(mActivity, R.drawable.login_radius_button)
                    val transitionDrawable =
                        TransitionDrawable(arrayOf(oldBackground, newBackground))
                    transitionDrawable.isCrossFadeEnabled = true
                    transitionDrawable.startTransition(300)
                    binding.followToggle.background = transitionDrawable
                }
            }

            val url = ApplicationClass.SERVER_URL + "${user.imageUrl}"
            Glide.with(this)
                .load(Uri.parse(url)).skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .apply {
                    into(binding.profileImage)
                }
            if (user.githubId != null && !user.githubId.isEmpty()) {
                binding.githubIdTv.text = "@" + user.githubId
                binding.githubIdTv.paintFlags = binding.githubIdTv.paintFlags or Paint.UNDERLINE_TEXT_FLAG
            } else {

            }
        }
        binding.githubIdTv.setOnClickListener {// 구글 검색 대응
            val intent = Intent(Intent.ACTION_WEB_SEARCH)
            intent.putExtra(SearchManager.QUERY, "https://github.com/" + user.githubId)
            startActivity(intent)
        }

        mActivity.loadingDialog.show()
        CoroutineScope(Dispatchers.IO).launch {
            likedPostList = RetrofitUtil.postService.getMyLikedPostList(user.id)
            myPageItemList = RetrofitUtil.myPageService.getMyPageItems(user.id)
            withContext(Dispatchers.Main) {
                devideByCategory()
                mActivity.loadingDialog.dismiss()
            }
        }

        binding.profileImage.setOnClickListener {

            showDialogEditProfile();
        }

    }

    private fun showDialogEditProfile(){
        val editProfileDialog = EditProfileDialog(user, mActivity, this, user.id != ApplicationClass.sharedPreferencesUtil.getUser().id)
        editProfileDialog.show(parentFragmentManager,"editProfileDialog")



//        val editMyPageDialog = EditMyPageDialog(user, mActivity)
//        editMyPageDialog.show(parentFragmentManager, "editMyPageDialog")

//        val builder:AlertDialog.Builder = AlertDialog.Builder(requireContext())
//            builder
//        binding.nickNameTv.text = user.nickname
//        binding.jobGroupTv.text = user.jobtype + " developer"
//        binding.jobYearTv.text = user.careerLevel
//        val url = ApplicationClass.SERVER_URL + "${user.imageUrl}"
//        Glide.with(this)
//            .load(Uri.parse(url))
//            .apply {
//                into(binding.profileImage)
//            }
    }

    private fun devideByCategory() {
        var mouseList = mutableListOf<LikedPost>()
        var keyboardList = mutableListOf<LikedPost>()
        var monitorList = mutableListOf<LikedPost>()
        var softwareList = mutableListOf<LikedPost>()
        var standList = mutableListOf<LikedPost>()
        var myPageList = mutableListOf<LikedPost>()
        for (post in likedPostList) {
            when(post.post.product.category) {
                "KEYBOARD" -> {
                    keyboardList.add(post)
                }
                "MOUSE" -> {
                    mouseList.add(post)
                }
                "MONITOR" -> {
                    monitorList.add(post)
                }
                "SOFTWARE" -> {
                    softwareList.add(post)
                }
                "STAND" -> {
                    standList.add(post)
                }
            }
        }
        for (mypage in myPageItemList) {
            myPageList.add(LikedPost(0, mypage.post))
            Log.d(TAG, "devideByCategory: ${mypage.post}")
        }

        adapter_keyboard = MyPageAdapter(mActivity)
        adapter_keyboard.submitList(keyboardList)
        binding.likedKeyboard.layoutManager = LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false).apply { reverseLayout = true; stackFromEnd = true }
        binding.likedKeyboard.adapter = adapter_keyboard
        adapter_keyboard.itemClickListener = object: MyPageAdapter.ItemClickListener {
            override fun onClick(view: View, position: Int, item: LikedPost) {
                pageViewModel.setPost(keyboardList[position].post)
                mActivity.changeFragment("postDetail")
            }
        }

        adapter_mouse = MyPageAdapter(mActivity)
        adapter_mouse.submitList(mouseList)
        binding.likedMouse.layoutManager = LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false).apply { reverseLayout = true; stackFromEnd = true }
        binding.likedMouse.adapter = adapter_mouse
        adapter_mouse.itemClickListener = object: MyPageAdapter.ItemClickListener {
            override fun onClick(view: View, position: Int, item: LikedPost) {
                pageViewModel.setPost(mouseList[position].post)
                mActivity.changeFragment("postDetail")
            }
        }

        adapter_monitor = MyPageAdapter(mActivity)
        adapter_monitor.submitList(monitorList)
        binding.likedMonitor.layoutManager = LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false).apply { reverseLayout = true; stackFromEnd = true }
        binding.likedMonitor.adapter = adapter_monitor
        adapter_monitor.itemClickListener = object: MyPageAdapter.ItemClickListener {
            override fun onClick(view: View, position: Int, item: LikedPost) {
                pageViewModel.setPost(monitorList[position].post)
                mActivity.changeFragment("postDetail")
            }
        }

        adapter_software = MyPageAdapter(mActivity)
        adapter_software.submitList(softwareList)
        binding.likedProgram.layoutManager = LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false).apply { reverseLayout = true; stackFromEnd = true }
        binding.likedProgram.adapter = adapter_software
        adapter_software.itemClickListener = object: MyPageAdapter.ItemClickListener {
            override fun onClick(view: View, position: Int, item: LikedPost) {
                pageViewModel.setPost(softwareList[position].post)
                mActivity.changeFragment("postDetail")
            }
        }

        adapter_mypage = MyPageAdapter(mActivity)
        adapter_mypage.submitList(myPageList)
        binding.myItem.layoutManager = LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false).apply { reverseLayout = true; stackFromEnd = true }
        binding.myItem.adapter = adapter_mypage
        adapter_mypage.itemClickListener = object: MyPageAdapter.ItemClickListener {
            override fun onClick(view: View, position: Int, item: LikedPost) {
                pageViewModel.setPost(myPageList[position].post)
                Log.d(TAG, "리스너 내부에서 : ${myPageList}")
                mActivity.changeFragment("postDetail")
            }
        }

        adapter_stand = MyPageAdapter(mActivity)
        adapter_stand.submitList(standList)
        binding.likedStand.layoutManager = LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false).apply { reverseLayout = true; stackFromEnd = true }
        binding.likedStand.adapter = adapter_stand
        adapter_stand.itemClickListener = object: MyPageAdapter.ItemClickListener {
            override fun onClick(view: View, position: Int, item: LikedPost) {
                pageViewModel.setPost(standList[position].post)
                mActivity.changeFragment("postDetail")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}