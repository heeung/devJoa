package com.ssafy.devJoa.page.search

import android.content.Context
import android.graphics.drawable.TransitionDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.ssafy.devJoa.R
import com.ssafy.devJoa.databinding.FragmentPostSearchBinding
import com.ssafy.devJoa.dto.Post
import com.ssafy.devJoa.page.PageActivity
import com.ssafy.devJoa.page.PageViewModel
import com.ssafy.devJoa.page.post.PostAdapter
import com.ssafy.devJoa.util.RetrofitUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchPostFragment : Fragment() {
    private var _binding: FragmentPostSearchBinding? = null
    private val binding get() = _binding!!

    lateinit var mActivity: PageActivity
    private val pageViewModel: PageViewModel by activityViewModels()

    lateinit var adapter: PostAdapter
//    lateinit var postList: List<Post>

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as PageActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    // 화면 구성 코드
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mActivity.nowPageFABToggle()
        init()
        binding.searchBtn.setOnClickListener {
            searchBtnClicked()
        }
    }

    private fun initFocusListener() {
        binding.searchEditTextField.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) { // 포커스가 되었을 때

            } else { // 포커스가 빠져나왔을 때
                binding.searchEditTextField.setText("")
                val inputMethodManager = mActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(binding.searchEditTextField.windowToken, 0)
            }
        }
    }

    private fun init() {
        initFocusListener()
        binding.searchEditTextField.imeOptions = EditorInfo.IME_ACTION_SEARCH
        binding.searchEditTextField.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                // 검색 버튼이 눌렸을 때 실행되는 코드 작성
                searchBtnClicked()
                true // 이벤트 처리 완료를 나타내기 위해 true 반환
            } else {
                false // 다른 액션 이벤트에 대해서는 처리하지 않음
            }
        }

        adapter = PostAdapter(mActivity)
        pageViewModel.searchPostList.observe(viewLifecycleOwner) {
            adapter.submitList(it)
            if (it.size == 0) {
                binding.sadImg.visibility = View.VISIBLE
            } else {
                binding.sadImg.visibility = View.GONE
            }
            mActivity.loadingDialog.dismiss()
        }

        val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        layoutManager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS
        binding.postRecyclerView.layoutManager = layoutManager
        binding.postRecyclerView.adapter = adapter
        adapter.itemClickListener = object: PostAdapter.ItemClickListener {
            override fun onClick(view: View, position: Int, item: Post) {
                mActivity.changeFragment("postDetail")
                pageViewModel.setPost(pageViewModel.searchPostList.value!![position])
                pageViewModel.viewPosition = position
            }
        }
        CoroutineScope(Dispatchers.IO).launch {
            pageViewModel.getSearchPostList()
        }
    }

    private fun searchBtnClicked() {
        mActivity.loadingDialog.show()
        val text = binding.searchEditTextField.text.toString()
        if (text.isEmpty()) {
            CoroutineScope(Dispatchers.IO).launch {
                pageViewModel.getSearchPostList()
            }
        } else {
            CoroutineScope(Dispatchers.IO).launch {
                pageViewModel.getSearchPostList(text)
            }
        }
        binding.reviewTextfield.clearFocus()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}