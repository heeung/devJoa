package com.ssafy.devJoa.page.post

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.snackbar.Snackbar
import com.ssafy.devJoa.R
import com.ssafy.devJoa.config.ApplicationClass
import com.ssafy.devJoa.databinding.PostItemBinding
import com.ssafy.devJoa.databinding.ProductItemBinding
import com.ssafy.devJoa.dto.Post
import com.ssafy.devJoa.dto.Product
import com.ssafy.devJoa.page.PageActivity
import com.ssafy.devJoa.page.product.ProductAdapter
import com.ssafy.devJoa.util.RetrofitUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PostAdapter(val context: Context): ListAdapter<Post, PostAdapter.PostViewHolder>(ItemComparator) {
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

    inner class PostViewHolder(var binding: PostItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindInfo(post: Post) {
            binding.productName.text = post.title
            binding.likeTv.text = post.likeCnt.toString()
            // Glide로 이미지 표시하기
            val url = ApplicationClass.SERVER_URL + "${post.product.imageUrl}"
            Glide.with(context)
                .load(Uri.parse(url))
                .into(binding.productImg)
    //            CoroutineScope(Dispatchers.IO).launch {
    //                val isMine = RetrofitUtil.myPageService.isMyItem((context as PageActivity).user.id, post.id)
    //                withContext(Dispatchers.Main) {
    //                    if (isMine == true) {
    //                        binding.item.setBackgroundResource(R.drawable.review_item_border)
    //                    }
    //                }
    //            }

            // 메인에 구현 위임
            binding.item.setOnClickListener {
                itemClickListener.onClick(it, layoutPosition, post)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostAdapter.PostViewHolder {
        val binding : PostItemBinding =
            PostItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostAdapter.PostViewHolder, position: Int) {
        holder.bindInfo(getItem(position))
    }
}