package com.devJoa.page.product

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.devJoa.config.ApplicationClass
import com.devJoa.databinding.ProductItemBinding
import com.devJoa.dto.Product

class ProductAdapter(val context: Context): ListAdapter<Product, ProductAdapter.ProductViewHolder>(ItemComparator) {
    companion object ItemComparator: DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    lateinit var itemClickListener: ItemClickListener
    interface ItemClickListener {
        fun onClick(view: View, position: Int, item: Product)
    }

    inner class ProductViewHolder(var binding: ProductItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindInfo(product: Product) {
            binding.productName.text = product.name
            binding.categoryTv.text = product.category
            // Glide로 이미지 표시하기
            val url = ApplicationClass.SERVER_URL + "${product.imageUrl}"
            Glide.with(context)
                .load(Uri.parse(url))
                .into(binding.productImg)

            // 메인에 구현 위임
            binding.item.setOnClickListener {
                itemClickListener.onClick(it, layoutPosition, product)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding : ProductItemBinding =
            ProductItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bindInfo(getItem(position))
    }
}