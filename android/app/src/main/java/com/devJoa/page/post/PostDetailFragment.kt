package com.devJoa.page.post

import android.annotation.SuppressLint
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.TransitionDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Half.toFloat
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ScrollView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.devJoa.R
import com.devJoa.config.ApplicationClass
import com.devJoa.databinding.FragmentPostDetailBinding
import com.devJoa.dto.*
import com.devJoa.page.PageActivity
import com.devJoa.page.PageViewModel
import com.devJoa.util.RetrofitUtil
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "PostDetailFragment_싸피"
class PostDetailFragment : Fragment() {
    private var _binding: FragmentPostDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter_review: ReviewAdapter

    private lateinit var mActivity: PageActivity
    private val pageViewModel: PageViewModel by activityViewModels()
    private lateinit var post: Post
    private lateinit var user: User
    private var isLiked = false
    private var isMyItem = false
    private var reviewPopdown = false
    private var chartPopdown = true

    private var event = "삽입"

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as PageActivity
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostDetailBinding.inflate(inflater, container, false)

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

        mActivity.loadingDialog.show()
        post = pageViewModel.post.value!!
        user = ApplicationClass.sharedPreferencesUtil.getUser()

        binding.linearLayoutCompat.visibility = View.GONE
        init()
        reviewListObserver()
        pageViewModel.getReviewList()
        initChart()
    }

    private fun init() {
        mActivity.nowPageFABToggle()
        reviewInsertTvClickListener()

        val url = ApplicationClass.SERVER_URL + "${post.product.imageUrl}"
        Glide.with(mActivity)
            .load(Uri.parse(url))
            .into(binding.productImg)

        pageViewModel.post.observe(viewLifecycleOwner) {
            binding.likeTv.text = it.likeCnt.toString()
            binding.productName.text = it.title
            lifecycleScope.launch {
                isLiked = RetrofitUtil.postService.isLikedByUser(user.id, it.id)
                Log.d(TAG, "init: ${isLiked}")
                if (isLiked) {
                    binding.likeImageView.setImageResource(R.drawable.outline_favorite_24)
                } else {
                    binding.likeImageView.setImageResource(R.drawable.outline_favorite_border_24)
                }
                isMyItem = RetrofitUtil.myPageService.isMyItem(user.id, it.product.id)
                if (isMyItem) {
                    binding.myItemToggleButton.text = "내 아이템에서 빼기"
//                    binding.myItemToggleButton.setBackgroundResource(R.drawable.login_radius_button_blue)
                    val oldBackground = binding.myItemToggleButton.background
                    val newBackground = ContextCompat.getDrawable(mActivity, R.drawable.login_radius_button_blue)
                    val transitionDrawable = TransitionDrawable(arrayOf(oldBackground, newBackground))
                    transitionDrawable.isCrossFadeEnabled = true
                    transitionDrawable.startTransition(300)
                    binding.myItemToggleButton.background = transitionDrawable
                } else {
                    binding.myItemToggleButton.text = "내 아이템에 추가"
//                    binding.myItemToggleButton.setBackgroundResource(R.drawable.login_radius_button)
                    val oldBackground = binding.myItemToggleButton.background
                    val newBackground = ContextCompat.getDrawable(mActivity, R.drawable.login_radius_button)
                    val transitionDrawable = TransitionDrawable(arrayOf(oldBackground, newBackground))
                    transitionDrawable.isCrossFadeEnabled = true
                    transitionDrawable.startTransition(300)
                    binding.myItemToggleButton.background = transitionDrawable
                }
                mActivity.loadingDialog.dismiss()
            }
            pageViewModel.getReviewList()
        }

        binding.likeLayout.setOnClickListener {
            lifecycleScope.launch {
                RetrofitUtil.postService.likeToggle(pageViewModel.post.value!!.id, user)
                val post = RetrofitUtil.postService.getPost(pageViewModel.post.value!!.id)
                pageViewModel.setPost(post)
                if (!isLiked) {
                    showSnackbar()
                }
            }
        }

        binding.myItemToggleButton.setOnClickListener {
            lifecycleScope.launch {
                RetrofitUtil.myPageService.myPageToggle(MyPageToggleKey(user.id, pageViewModel.post.value!!.id, pageViewModel.post.value!!.product.id))
                val post = RetrofitUtil.postService.getPost(pageViewModel.post.value!!.id)
                pageViewModel.setPost(post)
                if (!isMyItem) {
                    showSnackbar()
                }
            }
        }

        binding.searchOnGoolgeTv.setOnClickListener {// 구글 검색 대응
            val intent = Intent(Intent.ACTION_WEB_SEARCH)
            intent.putExtra(SearchManager.QUERY, binding.productName.text.toString())
            startActivity(intent)
        }

        binding.reviewInsertBtn.setOnClickListener {
            if (binding.reviewEditTextfield.text.toString().isEmpty()) {
                binding.reviewTextfield.error = "한 글자 이상 입력하세요"
            } else {
                CoroutineScope(Dispatchers.IO).launch {
                    val content = binding.reviewEditTextfield.text.toString()
                    RetrofitUtil.reviewService.insertReview(InsertReview(content, post.id, user.id))
                    val newPost = RetrofitUtil.postService.getPost(post.id)
                    withContext(Dispatchers.Main) {
                        event = "삽입"
                        pageViewModel.setPost(newPost)
                        binding.reviewEditTextfield.setText("")
                    }
                }
            }
        }
    }

    private fun reviewListObserver() {
        adapter_review = ReviewAdapter(mActivity)
        binding.reviewRecycler.adapter = adapter_review
        binding.reviewRecycler.layoutManager = LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false)
        adapter_review.itemClickListener = object: ReviewAdapter.ItemClickListener {
            override fun onClick(view: View, position: Int, item: Review) {

            }
        }
        adapter_review.modifyDoneClickListener = object: ReviewAdapter.ModifyDoneClickListener {
            override fun onClick(view: View, position: Int, item: Review, content: String) {
//                mActivity.showToast("수정 버튼 클릭 !!!!!!!! ${item.id} :: ${item.content}")
                CoroutineScope(Dispatchers.IO).launch {
                    val newPost = RetrofitUtil.reviewService.modifyReview(item.id, InsertReview(item.id, content, post.id, item.member.id))
                    withContext(Dispatchers.Main) {
                        event = "수정"
                        pageViewModel.setPost(newPost)
                    }
                }
            }
        }
        adapter_review.deleteClickListener = object: ReviewAdapter.DeleteClickListener {
            override fun onClick(view: View, position: Int, item: Review) {
                showDeleteSnackbar(item)
            }
        }
        adapter_review.userClickListener = object : ReviewAdapter.UserClickListener {
            override fun onClick(view: View, position: Int, user: User) {
                pageViewModel.setUser(user)
                mActivity.changeFragment("myPage")
            }
        }
        pageViewModel.reviewList.observe(viewLifecycleOwner) {
            binding.reviewTv.text = it.size.toString()
            if (it.size == 0) {
                binding.firstReviewGoTv.visibility = View.VISIBLE
            } else {
                binding.firstReviewGoTv.visibility = View.INVISIBLE
            }
            adapter_review.submitList(pageViewModel.reviewList.value)
            if (event == "삽입") {
                view?.post {
                    binding.reviewRecycler.smoothScrollToPosition(0)
                }
            }
        }
    }

    private fun reviewInsertTvClickListener() {
        binding.reviewInsertTv.setOnClickListener {
            when(reviewPopdown) {
                true -> {
                    binding.linearLayoutCompat.animate()
                        .alpha(0f)
                        .setDuration(150)
                        .withEndAction {
                            binding.linearLayoutCompat.visibility = View.GONE
                        }
                    binding.reviewArrow.setImageResource(R.drawable.baseline_arrow_drop_down_24)
                    binding.reviewInsertTv.text = "댓글 쓰기"
                    reviewPopdown = false
                }
                false -> {
                    binding.detailPage.post {
                        binding.detailPage.fullScroll(ScrollView.FOCUS_DOWN)
                    }
                    binding.linearLayoutCompat.visibility = View.VISIBLE
                    binding.linearLayoutCompat.alpha = 0f
                    binding.linearLayoutCompat.animate()
                        .alpha(1f)
                        .setDuration(150)
                    binding.reviewArrow.setImageResource(R.drawable.baseline_arrow_drop_up_24)
                    binding.reviewInsertTv.text = "닫기"
                    reviewPopdown = true
                }
            }
        }
    }

    private fun chartToggleListener() {
        binding.statisticToggleTv.setOnClickListener {
            when(chartPopdown) {
                true -> {
                    binding.chart.animate()
                        .alpha(0f)
                        .setDuration(150)
                        .withEndAction {
                            binding.chart.visibility = View.GONE
                        }
                    binding.statisticArrow.setImageResource(R.drawable.baseline_arrow_drop_down_24)
                    binding.statisticToggleTv.text = "통계 보기"
                    chartPopdown = false
                }
                false -> {
                    binding.detailPage.post {
                        val middleY = binding.detailPage.getChildAt(0).height / 2
                        binding.detailPage.smoothScrollTo(0, middleY)
                    }
                    binding.chart.visibility = View.VISIBLE
                    binding.chart.alpha = 0f
                    binding.chart.animate()
                        .alpha(1f)
                        .setDuration(150)
                    binding.statisticArrow.setImageResource(R.drawable.baseline_arrow_drop_up_24)
                    binding.statisticToggleTv.text = "닫기"
                    chartPopdown = true
                }
            }
        }
    }

    private fun initChart() {
        if (post.likeCnt == 0) {
            binding.chart.visibility = View.GONE
            binding.statisticArrow.setImageResource(R.drawable.baseline_arrow_drop_down_24)
            binding.statisticToggleTv.text = "통계 보기"
            chartPopdown = false
        }

        chartToggleListener()

        CoroutineScope(Dispatchers.IO).launch {
            val map = RetrofitUtil.postService.getStatistics(post.id)
            Log.d(TAG, "initChart: ${map}")
            withContext(Dispatchers.Main) {
                with(binding.chart) {
                    setUsePercentValues(true)
                    description.isEnabled = false
                    setExtraOffsets(5f, 10f, 5f, 5f)
                    isDrawHoleEnabled = true
                    setHoleColor(Color.WHITE)
                    transparentCircleRadius = 61f

                    val yValues: ArrayList<PieEntry> = ArrayList()
                    with(yValues) {
                        add(PieEntry(map["BACKEND"]!!.toFloat(), "백엔드"))
                        add(PieEntry(map["FRONTEND"]!!.toFloat(), "프론트엔드"))
                        add(PieEntry(map["MOBILE"]!!.toFloat(), "모바일"))
                        add(PieEntry(map["ETC"]!!.toFloat(), "기타"))

                        setEntryLabelColor(Color.BLACK)
                    }

                    animateY(1000, Easing.EaseInOutCubic)

                    val dataSet: PieDataSet = PieDataSet(yValues, "")
                    with(dataSet) {
                        sliceSpace = 4f
                        selectionShift = 10f
                        setColors(*ColorTemplate.MATERIAL_COLORS)
                    }

                    val pieData: PieData = PieData(dataSet)
                    with(pieData) {
                        setValueTextSize(10f)
                        setValueTextColor(Color.BLACK)
                        val des = Description()
                        des.text = "현재 아이템을 좋아하는 사용자 비율"
                        description = des
                    }
                    data = pieData
                }
            }
        }
    }

    private fun showSnackbar() {
        val snackbar = Snackbar.make(binding.root, "아이템이 추가되었습다.", Snackbar.LENGTH_LONG)
        snackbar.setBackgroundTint(ContextCompat.getColor(mActivity, R.color.sky_blue_color_darker))
        snackbar.setActionTextColor(ContextCompat.getColor(mActivity, R.color.black))
        snackbar.setAction("확인하러 가기 ->") {
            pageViewModel.setUser(ApplicationClass.sharedPreferencesUtil.getUser())
            mActivity.changeFragment("myPage")
        }
        snackbar.show()
    }

    private fun showDeleteSnackbar(item: Review) {
        val snackbar = Snackbar.make(binding.root, "댓글을 삭제 하시겠습니까?", Snackbar.LENGTH_LONG)
        snackbar.setBackgroundTint(ContextCompat.getColor(mActivity, R.color.error_color))
        snackbar.setActionTextColor(ContextCompat.getColor(mActivity, R.color.black))
        snackbar.setAction("삭제하기") {
            CoroutineScope(Dispatchers.IO).launch {
                RetrofitUtil.reviewService.deleteReview(InsertReview(item.id, post.id, item.member.id))
                val newPost = RetrofitUtil.postService.getPost(post.id)
                withContext(Dispatchers.Main) {
                    event = "삭제"
                    pageViewModel.setPost(newPost)
                }
            }
        }
        snackbar.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}