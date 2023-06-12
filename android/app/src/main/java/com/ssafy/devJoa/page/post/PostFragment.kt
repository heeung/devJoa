package com.ssafy.devJoa.page.post

import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide.init
import com.google.android.material.chip.Chip
import com.ssafy.devJoa.R
import com.ssafy.devJoa.config.ApplicationClass
import com.ssafy.devJoa.databinding.FragmentPostBinding
import com.ssafy.devJoa.databinding.FragmentProductBinding
import com.ssafy.devJoa.dto.Post
import com.ssafy.devJoa.dto.Product
import com.ssafy.devJoa.page.PageActivity
import com.ssafy.devJoa.page.PageViewModel
import com.ssafy.devJoa.page.product.ProductAdapter
import com.ssafy.devJoa.util.RetrofitUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "PostFragment_싸피"
class PostFragment : Fragment() {
    private var _binding: FragmentPostBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: PostAdapter

    private val pageViewModel: PageViewModel by activityViewModels()
    lateinit var mActivity: PageActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as PageActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    // 화면 구성 코드
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mActivity.nowPageFABToggle()
        categorySearchListener()
        init()
        mActivity.binding.scrollResetBtn.setOnClickListener {
            val smooth = object : LinearSmoothScroller(binding.postRecyclerView.context) {
                override fun getVerticalSnapPreference(): Int {
                    return SNAP_TO_START
                }

                override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics?): Float {
                    return 0.05f
                }
            }
            smooth.targetPosition = 0
            binding.postRecyclerView.layoutManager?.startSmoothScroll(smooth)
        }
    }

    private fun selectInitialChip() {
        when (pageViewModel.selectedCategory) {
            "KEYBOARD" -> {
                binding.chip1.isChecked = true
            }
            "MOUSE" -> {
                binding.chip2.isChecked = true
            }
            "MONITOR" -> {
                binding.chip3.isChecked = true
            }
            "SOFTWARE" -> {
                binding.chip4.isChecked = true
            }
            "STAND" -> {
                binding.chip5.isChecked = true
            }
        }
    }

    private fun init() {
        selectInitialChip()

        adapter = PostAdapter(mActivity)
        pageViewModel.postList.observe(viewLifecycleOwner) {
            adapter.submitList(pageViewModel.postList.value)
            val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            layoutManager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS
            binding.postRecyclerView.layoutManager = layoutManager
            binding.postRecyclerView.adapter = adapter
            adapter.itemClickListener = object: PostAdapter.ItemClickListener {
                override fun onClick(view: View, position: Int, item: Post) {
                    mActivity.changeFragment("postDetail")
                    pageViewModel.setPost(pageViewModel.postList.value!![position])
                    pageViewModel.viewPosition = position
                }
            }
            mActivity.loadingDialog.dismiss()
            binding.postRecyclerView.scrollToPosition(pageViewModel.viewPosition)
        }

        mActivity.loadingDialog.show()
        CoroutineScope(Dispatchers.IO).launch {
            Log.d(TAG, "백키로 돌아왔을때 동작 : ${binding.chipGroup.checkedChipId} ==? ${View.NO_ID}")
            when (pageViewModel.selectedCategory) {
                "KEYBOARD" -> {
                    pageViewModel.getPostListByCategory("KEYBOARD")
                }
                "MOUSE" -> {
                    pageViewModel.getPostListByCategory("MOUSE")
                }
                "MONITOR" -> {
                    pageViewModel.getPostListByCategory("MONITOR")
                }
                "SOFTWARE" -> {
                    pageViewModel.getPostListByCategory("SOFTWARE")
                }
                "STAND" -> {
                    pageViewModel.getPostListByCategory("STAND")
                }
                "NONE" -> {
                    pageViewModel.getPostList()
                }
            }
        }
    }

    private fun categorySearchListener() {
        binding.chipGroup.setOnCheckedStateChangeListener { _, checkedId ->
            mActivity.loadingDialog.show()
            CoroutineScope(Dispatchers.IO).launch {
                if (checkedId.size != 0) {
                    when (checkedId[0]) {
                        binding.chip1.id -> {
//                        mActivity.showToast("키보드")
                            pageViewModel.getPostListByCategory("KEYBOARD")
                        }
                        binding.chip2.id -> {
//                        mActivity.showToast("마우스")
                            pageViewModel.getPostListByCategory("MOUSE")
                        }
                        binding.chip3.id -> {
//                        mActivity.showToast("모니터")
                            pageViewModel.getPostListByCategory("MONITOR")
                        }
                        binding.chip4.id -> {
//                        mActivity.showToast("소프트웨어")
                            pageViewModel.getPostListByCategory("SOFTWARE")
                        }
                        binding.chip5.id -> {
//                        mActivity.showToast("스탠드")
                            pageViewModel.getPostListByCategory("STAND")
                        }
                    }
                } else {
//                mActivity.showToast("선택 안됨")
                    pageViewModel.getPostList()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}