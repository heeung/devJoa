package com.ssafy.devJoa.page.home

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
import com.ssafy.devJoa.R
import com.ssafy.devJoa.config.ApplicationClass
import com.ssafy.devJoa.databinding.PostItemBinding
import com.ssafy.devJoa.databinding.ProductItemBinding
import com.ssafy.devJoa.databinding.Top5ItemBinding
import com.ssafy.devJoa.databinding.Top5UserItemBinding
import com.ssafy.devJoa.dto.Post
import com.ssafy.devJoa.dto.Product
import com.ssafy.devJoa.dto.User
import com.ssafy.devJoa.page.PageActivity
import com.ssafy.devJoa.page.product.ProductAdapter
import kotlinx.coroutines.withContext

class Top5UserAdapter(val context: Context): ListAdapter<User, Top5UserAdapter.UserViewHolder>(ItemComparator) {
    companion object ItemComparator: DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }
        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    lateinit var itemClickListener: ItemClickListener
    interface ItemClickListener {
        fun onClick(view: View, position: Int, item: User)
    }

    inner class UserViewHolder(var binding: Top5UserItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindInfo(user: User) {
            binding.userNickname.text = user.nickname
            binding.followTv.text = user.followerCount.toString()
            binding.userJobtype.text = user.jobtype + " developer"
            binding.userLevel.text = user.careerLevel
            // Glide로 이미지 표시하기
            val url = ApplicationClass.SERVER_URL + "${user.imageUrl}"
            Glide.with(context)
                .load(Uri.parse(url)).skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(binding.userImg)

            // 메인에 구현 위임
            binding.item.setOnClickListener {
                itemClickListener.onClick(it, layoutPosition, user)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Top5UserAdapter.UserViewHolder {
        val binding : Top5UserItemBinding =
            Top5UserItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: Top5UserAdapter.UserViewHolder, position: Int) {
        holder.bindInfo(getItem(position))
    }
}