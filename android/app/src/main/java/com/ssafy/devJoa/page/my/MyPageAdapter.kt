package com.ssafy.devJoa.page.my

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
import com.ssafy.devJoa.config.ApplicationClass
import com.ssafy.devJoa.databinding.MypageItemBinding
import com.ssafy.devJoa.databinding.PostItemBinding
import com.ssafy.devJoa.databinding.ProductItemBinding
import com.ssafy.devJoa.dto.LikedPost
import com.ssafy.devJoa.dto.Post
import com.ssafy.devJoa.dto.Product
import com.ssafy.devJoa.page.product.ProductAdapter
import kotlinx.coroutines.withContext

class MyPageAdapter(val context: Context): ListAdapter<LikedPost, MyPageAdapter.PostViewHolder>(ItemComparator) {
    companion object ItemComparator: DiffUtil.ItemCallback<LikedPost>() {
        override fun areItemsTheSame(oldItem: LikedPost, newItem: LikedPost): Boolean {
            return oldItem == newItem
        }
        override fun areContentsTheSame(oldItem: LikedPost, newItem: LikedPost): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    lateinit var itemClickListener: ItemClickListener
    interface ItemClickListener {
        fun onClick(view: View, position: Int, item: LikedPost)
    }

    inner class PostViewHolder(var binding: MypageItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindInfo(likedPost: LikedPost) {
            // 글자수 초과 방지
            binding.productName.text = likedPost.post.title
            // Glide로 이미지 표시하기
            val url = ApplicationClass.SERVER_URL + "${likedPost.post.product.imageUrl}"
            Glide.with(context)
                .load(Uri.parse(url))
                .into(binding.productImg)

            // 메인에 구현 위임
            binding.item.setOnClickListener {
                itemClickListener.onClick(it, layoutPosition, likedPost)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyPageAdapter.PostViewHolder {
        val binding : MypageItemBinding =
            MypageItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyPageAdapter.PostViewHolder, position: Int) {
        holder.bindInfo(getItem(position))
    }
}