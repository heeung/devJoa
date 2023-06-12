package com.devJoa.page.product

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.devJoa.databinding.FragmentProductBinding
import com.devJoa.dto.Product
import com.devJoa.page.PageActivity
import com.devJoa.util.RetrofitUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProductFragment : Fragment() {
    private var _binding: FragmentProductBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ProductAdapter
    private lateinit var productList: List<Product>

    lateinit var mActivity: PageActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as PageActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    // 화면 구성 코드
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }

    private fun init() {
        adapter = ProductAdapter(mActivity)
        CoroutineScope(Dispatchers.IO).launch {
            productList = RetrofitUtil.productService.getProductList()
            withContext(Dispatchers.Main) {
                adapter.submitList(productList)
                binding.productRecyclerView.layoutManager = GridLayoutManager(mActivity, 3)
                binding.productRecyclerView.adapter = adapter
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}