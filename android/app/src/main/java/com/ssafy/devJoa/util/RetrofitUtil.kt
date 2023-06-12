package com.ssafy.devJoa.util

import com.ssafy.devJoa.api.*
import com.ssafy.devJoa.config.ApplicationClass

class RetrofitUtil {
    companion object{
        val userService = ApplicationClass.retrofit.create(UserService::class.java)
        val productService = ApplicationClass.retrofit.create(ProductService::class.java)
        val postService = ApplicationClass.retrofit.create(PostService::class.java)
        val myPageService = ApplicationClass.retrofit.create(MyPageService::class.java)
        val reviewService = ApplicationClass.retrofit.create(ReviewService::class.java)
        val fileService = ApplicationClass.retrofit.create(FileService::class.java)
        val followService = ApplicationClass.retrofit.create(FollowService::class.java)
    }
}