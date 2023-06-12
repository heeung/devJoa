package com.devJoa.page.post

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.TransitionDrawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.devJoa.R
import com.devJoa.config.ApplicationClass
import com.devJoa.databinding.ReviewItemBinding
import com.devJoa.dto.Review
import com.devJoa.dto.User
import com.devJoa.page.PageActivity
import java.util.*

class ReviewAdapter(val context: Context): ListAdapter<Review, ReviewAdapter.ReviewViewHolder>(ItemComparator) {
    companion object ItemComparator: DiffUtil.ItemCallback<Review>() {
        override fun areItemsTheSame(oldItem: Review, newItem: Review): Boolean {
            return oldItem == newItem
        }
        override fun areContentsTheSame(oldItem: Review, newItem: Review): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    lateinit var oldContent: String

    lateinit var itemClickListener: ItemClickListener
    interface ItemClickListener {
        fun onClick(view: View, position: Int, item: Review)
    }
    lateinit var modifyDoneClickListener: ModifyDoneClickListener
    interface ModifyDoneClickListener {
        fun onClick(view: View, position: Int, item: Review, content: String)
    }
    lateinit var deleteClickListener: DeleteClickListener
    interface DeleteClickListener {
        fun onClick(view: View, position: Int, item: Review)
    }
    lateinit var userClickListener: UserClickListener
    interface UserClickListener {
        fun onClick(view: View, position: Int, user: User)
    }

    inner class ReviewViewHolder(var binding: ReviewItemBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("ClickableViewAccessibility")
        fun bindInfo(review: Review) {
            binding.nickNameTv.text = review.member.nickname
            binding.contentTv.setText(review.content)
            binding.dateTv.text = review.createDate.subSequence(2, 10)
            when (review.member.jobtype) {
                "BACKEND" -> {
                    binding.jobTypeTv.text = "백엔드 개발자"
                }
                "FRONTEND" -> {
                    binding.jobTypeTv.text = "프론트엔드 개발자"
                }
                "MOBILE" -> {
                    binding.jobTypeTv.text = "모바일 개발자"
                }
                "ETC" -> {
                    binding.jobTypeTv.text = "기타 직군"
                }
            }
            binding.contentTv.setOnTouchListener { v, event -> true }
            val url = ApplicationClass.SERVER_URL + "${review.member.imageUrl}"
            Glide.with(context as PageActivity)
                .load(Uri.parse(url)).skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .apply {
                    into(binding.profileImage)
                }

            if ((context as PageActivity).user.id == review.member.id) {
                binding.modifyBtn.visibility = View.VISIBLE
                binding.deleteBtn.visibility = View.VISIBLE
            } else {
                binding.modifyBtn.visibility = View.GONE
                binding.deleteBtn.visibility = View.GONE
            }

            binding.contentTv.setOnFocusChangeListener { view, hasFocus ->
                if (hasFocus) { // 포커스가 되었을 때
                    binding.doneBtn.visibility = View.VISIBLE
                    oldContent = binding.contentTv.text.toString()

                    val oldBackground = binding.item.background
                    val newBackground = ContextCompat.getDrawable(context, R.drawable.post_item_border)
                    val transitionDrawable = TransitionDrawable(arrayOf(oldBackground, newBackground))
                    transitionDrawable.isCrossFadeEnabled = true
                    transitionDrawable.startTransition(500)
                    binding.item.background = transitionDrawable

                } else { // 포커스가 빠져나왔을 때
                    binding.doneBtn.visibility = View.GONE
                    binding.contentTv.setText(oldContent)

                    val oldBackground = binding.item.background
                    val newBackground = ContextCompat.getDrawable(context, R.drawable.review_item_border)
                    val transitionDrawable = TransitionDrawable(arrayOf(oldBackground, newBackground))
                    transitionDrawable.isCrossFadeEnabled = true
                    transitionDrawable.startTransition(500)
                    binding.item.background = transitionDrawable
                }
            }
//            binding.contentTv.setOnTouchListener(null)
            // 메인에 구현 위임
            binding.item.setOnClickListener {
                itemClickListener.onClick(it, layoutPosition, review)
            }
            binding.modifyBtn.setOnClickListener {// 누르면 포커스 주면서 텍스트 맨 뒤로 커서 보내는 코드
                binding.contentTv.requestFocus()
                val imm = binding.contentTv.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(binding.contentTv, InputMethodManager.SHOW_IMPLICIT)
                binding.contentTv.setSelection(binding.contentTv.text!!.length) // 커서 맨 뒤로 이동
                binding.contentTv.performClick() // 포커스 주기
            }
            binding.doneBtn.setOnClickListener {
                modifyDoneClickListener.onClick(it, layoutPosition, review, binding.contentTv.text.toString())
                binding.contentTv.clearFocus()
            }
            binding.deleteBtn.setOnClickListener {
                deleteClickListener.onClick(it, layoutPosition, review)
            }
            binding.profileLayout.setOnClickListener {
                userClickListener.onClick(it, layoutPosition, review.member)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewAdapter.ReviewViewHolder {
        val binding : ReviewItemBinding =
            ReviewItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReviewViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReviewAdapter.ReviewViewHolder, position: Int) {
        holder.bindInfo(getItem(position))
    }
}