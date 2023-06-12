package com.ssafy.devJoa.page.home

import android.content.Context
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.DisplayMetrics
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import com.ssafy.devJoa.MainActivity
import com.ssafy.devJoa.R
import com.ssafy.devJoa.databinding.FragmentHomeBinding
import com.ssafy.devJoa.databinding.FragmentLoginBinding
import com.ssafy.devJoa.databinding.PostItemBinding
import com.ssafy.devJoa.dto.LikedPost
import com.ssafy.devJoa.dto.Post
import com.ssafy.devJoa.dto.User
import com.ssafy.devJoa.page.PageActivity
import com.ssafy.devJoa.page.PageViewModel
import com.ssafy.devJoa.page.my.MyPageAdapter
import com.ssafy.devJoa.page.post.PostAdapter
import com.ssafy.devJoa.util.RetrofitUtil
import kotlinx.coroutines.*
import java.util.*

private const val TAG = "HomeFragment_싸피"
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding
    private val pageViewModel: PageViewModel by activityViewModels()

    lateinit var mActivity: PageActivity

    private lateinit var adapter_post: Top5PostAdapter
    private lateinit var adapter_user: Top5UserAdapter
    private lateinit var top5ItemList: List<Post>
    private lateinit var top5UserList: List<User>

    private var timer1: Timer? = null
    private var timer2: Timer? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
//        _binding = FragmentHomeBinding.inflate(layoutInflater)
        mActivity = context as PageActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding?.root
    }

    // 화면 구성 코드
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        mActivity.nowPageFABToggle()
        mActivity.changeFABsColor("homePage")
        init()
        autoRecycler()
    }

    private fun init() {
        mActivity.loadingDialog.show()
        CoroutineScope(Dispatchers.IO).launch {
            top5ItemList = RetrofitUtil.postService.getTop5PostList()
            top5UserList = RetrofitUtil.userService.getTop5User()
            withContext(Dispatchers.Main) {
//                delay(100)
                initPostAdapter()
                initUserAdapter()
//                autoRecycler()
                mActivity.loadingDialog.dismiss()
            }
        }
    }

    private fun initPostAdapter() {
        adapter_post = Top5PostAdapter(mActivity)
        adapter_post.submitList(top5ItemList)
        binding?.bestItem?.layoutManager = LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false)
        binding?.bestItem?.adapter = adapter_post
        adapter_post.itemClickListener = object: Top5PostAdapter.ItemClickListener {
            override fun onClick(view: View, position: Int, item: Post) {
                pageViewModel.setPost(top5ItemList[position])
                mActivity.changeFragment("postDetail")
            }
        }
    }

    private fun initUserAdapter() {
        adapter_user = Top5UserAdapter(mActivity)
        adapter_user.submitList(top5UserList)
        binding?.bestUser?.layoutManager = LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false)
        binding?.bestUser?.adapter = adapter_user
        adapter_user.itemClickListener = object: Top5UserAdapter.ItemClickListener {
            override fun onClick(view: View, position: Int, item: User) {
                pageViewModel.setUser(item)
                mActivity.changeFragment("myPage")
            }
        }
    }

    private fun autoRecycler() {
        val scrollSpeed1 = 100f // 스크롤 속도 설정 (픽셀/초)
        val scrollSpeed2 = 90f // 스크롤 속도 설정 (픽셀/초)
        val scrollDelay1 = 2500L // 스크롤 딜레이 설정 (1초마다 스크롤 시작)
        val scrollDelay2 = 2400L // 스크롤 딜레이 설정 (1초마다 스크롤 시작)

        timer1 = Timer()
        timer2 = Timer()
        val scrollTask1 = object : TimerTask() {
            override fun run() {
                val layoutManager = binding?.bestItem?.layoutManager as LinearLayoutManager
                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                val itemCount = adapter_post.itemCount

                val smoothScroller = object : LinearSmoothScroller(mActivity) {
                    override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
                        return scrollSpeed1 / displayMetrics.densityDpi
                    }
                }
                if (lastVisibleItemPosition == itemCount - 1) {
                    // 스크롤이 마지막 항목까지 도달한 경우, 맨 처음 항목으로 스크롤
                    smoothScroller.targetPosition = 0
                } else {
                    // 다음 항목으로 스크롤
                    smoothScroller.targetPosition = lastVisibleItemPosition + 1
                }
                layoutManager.startSmoothScroll(smoothScroller)
            }
        }
        val scrollTask2 = object : TimerTask() {
            override fun run() {
                val layoutManager = binding?.bestUser?.layoutManager as LinearLayoutManager
                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                val itemCount = adapter_user.itemCount

                val smoothScroller = object : LinearSmoothScroller(mActivity) {
                    override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
                        return scrollSpeed2 / displayMetrics.densityDpi
                    }
                }
                if (lastVisibleItemPosition == itemCount - 1) {
                    // 스크롤이 마지막 항목까지 도달한 경우, 맨 처음 항목으로 스크롤
                    smoothScroller.targetPosition = 0
                } else {
                    // 다음 항목으로 스크롤
                    smoothScroller.targetPosition = lastVisibleItemPosition + 1
                }
                layoutManager.startSmoothScroll(smoothScroller)
            }
        }

        timer1!!.scheduleAtFixedRate(scrollTask1, scrollDelay1, scrollDelay1)
        timer2!!.scheduleAtFixedRate(scrollTask2, scrollDelay2, scrollDelay2)
    }

    private fun stopAutoRecycler() {
        timer1?.cancel()
        timer2?.cancel()
        timer1 = null
        timer2 = null
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroyView() {
        Log.d(TAG, "onDestroyView: ${_binding}")
        super.onDestroyView()
        _binding = null
        stopAutoRecycler()
    }
}