package com.devJoa.page.search

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.devJoa.databinding.FragmentUserSearchBinding
import com.devJoa.dto.User
import com.devJoa.page.PageActivity
import com.devJoa.page.PageViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SearchUserFragment : Fragment() {
    private var _binding: FragmentUserSearchBinding? = null
    private val binding get() = _binding!!

    lateinit var mActivity: PageActivity
    lateinit var adapter: UserAdapter

    private val pageViewModel: PageViewModel by activityViewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as PageActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    // 화면 구성 코드
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mActivity.nowPageFABToggle()
        binding.sadImg.visibility = View.INVISIBLE
        init()
        if (pageViewModel.searchUserList.value != null && pageViewModel.searchUserList.value!!.size != 0) {
            binding.goSearchBtnTv.visibility = View.INVISIBLE
        }
        binding.searchBtn.setOnClickListener {
            binding.sadImg.visibility = View.INVISIBLE
            searchBtnClicked()
        }
        binding.sadImg.visibility = View.INVISIBLE
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

        adapter = UserAdapter(mActivity)
        pageViewModel.searchUserList.observe(viewLifecycleOwner) {
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
        binding.userRecyclerView.layoutManager = layoutManager
        binding.userRecyclerView.adapter = adapter
        adapter.itemClickListener = object: UserAdapter.ItemClickListener {
            override fun onClick(view: View, position: Int, item: User) {
                pageViewModel.setUser(item)
                mActivity.changeFragment("myPage")
                pageViewModel.viewPosition = position
            }
        }
    }

    override fun onPause() {
        super.onPause()
    }

    private fun searchBtnClicked() {
        binding.goSearchBtnTv.visibility = View.INVISIBLE
        mActivity.loadingDialog.show()
        val text = binding.searchEditTextField.text.toString()
        if (text.isEmpty()) {
            CoroutineScope(Dispatchers.IO).launch {
                pageViewModel.getSearchUserList()
            }
        } else {
            CoroutineScope(Dispatchers.IO).launch {
                pageViewModel.getSearchUserList(text)
            }
        }
        binding.reviewTextfield.clearFocus()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}