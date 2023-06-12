package com.devJoa.page.home

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
import com.devJoa.databinding.Top5ItemBinding
import com.devJoa.dto.Post

class Top5PostAdapter(val context: Context): ListAdapter<Post, Top5PostAdapter.PostViewHolder>(ItemComparator) {
    companion object ItemComparator: DiffUtil.ItemCallback<Post>() {
        override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem == newItem
        }
        override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    lateinit var itemClickListener: ItemClickListener
    interface ItemClickListener {
        fun onClick(view: View, position: Int, item: Post)
    }

    inner class PostViewHolder(var binding: Top5ItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindInfo(post: Post) {
            binding.productName.text = "${position + 1}. " + post.title
            binding.likeTv.text = post.likeCnt.toString()
            // Glide로 이미지 표시하기
            val url = ApplicationClass.SERVER_URL + "${post.product.imageUrl}"
            Glide.with(context)
                .load(Uri.parse(url))
                .into(binding.productImg)

            // 메인에 구현 위임
            binding.item.setOnClickListener {
                itemClickListener.onClick(it, layoutPosition, post)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Top5PostAdapter.PostViewHolder {
        val binding : Top5ItemBinding =
            Top5ItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: Top5PostAdapter.PostViewHolder, position: Int) {
        holder.bindInfo(getItem(position))
    }
}