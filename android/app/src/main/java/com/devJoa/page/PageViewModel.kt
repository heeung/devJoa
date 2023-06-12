package com.devJoa.page

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devJoa.dto.Post
import com.devJoa.dto.Review
import com.devJoa.dto.User
import com.devJoa.util.RetrofitUtil
import kotlinx.coroutines.launch

class PageViewModel: ViewModel() {

    private val _user = MutableLiveData<User>()
    val user: LiveData<User>
        get() = _user

    fun setUser(user: User) {
        _user.value = user
    }

    private val _postList = MutableLiveData<List<Post>>()
    val postList: LiveData<List<Post>>
        get() = _postList
    suspend fun getPostList() {
        viewModelScope.launch {
            _postList.value = RetrofitUtil.postService.getPostList()
        }
        selectedCategory = "NONE"
    }
    suspend fun getPostListByCategory(category: String) {
        viewModelScope.launch {
            _postList.value = RetrofitUtil.postService.getPostListByCategory(category)
        }
        selectedCategory = category
    }

    private val _searchPostList = MutableLiveData<List<Post>>()
    val searchPostList: LiveData<List<Post>>
        get() = _searchPostList
    suspend fun getSearchPostList() {
        viewModelScope.launch {
            _searchPostList.value = RetrofitUtil.postService.getPostList()
        }
    }
    suspend fun getSearchPostList(str: String) {
        viewModelScope.launch {
            _searchPostList.value = RetrofitUtil.postService.searchPostList(str)
        }
    }

    private val _searchUserList = MutableLiveData<List<User>>()
    val searchUserList: LiveData<List<User>>
        get() = _searchUserList
    suspend fun getSearchUserList() {
        viewModelScope.launch {
            _searchUserList.value = RetrofitUtil.userService.getUserList()
        }
    }
    suspend fun getSearchUserList(str: String) {
        viewModelScope.launch {
            _searchUserList.value = RetrofitUtil.userService.searchUserList(str)
        }
    }

    private val _post = MutableLiveData<Post>()
    val post: LiveData<Post>
        get() = _post

    fun setPost(post: Post) {
        _post.value = post
    }

    private val _reviewList = MutableLiveData<List<Review>>()
    val reviewList: LiveData<List<Review>>
        get() = _reviewList
    fun getReviewList() {
        _reviewList.value = post.value!!.reivewList
    }

    var selectedCategory = "NONE"
    var viewPosition = 0
}