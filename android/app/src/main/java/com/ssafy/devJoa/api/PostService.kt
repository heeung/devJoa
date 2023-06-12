package com.ssafy.devJoa.api

import com.ssafy.devJoa.dto.LikedPost
import com.ssafy.devJoa.dto.Post
import com.ssafy.devJoa.dto.User
import com.ssafy.devJoa.dto.UserLogin
import retrofit2.http.*

interface PostService {
    // 상품 전체조회
    @GET("/rest/posts")
    suspend fun getPostList(): List<Post>

    // 상품 단건조회
    @GET("/rest/posts/{postId}")
    suspend fun getPost(@Path("postId") postId: Int): Post

    // 좋아요 기능
    @POST("/rest/posts/like/{postId}")
    suspend fun likeToggle(@Path("postId") postId: Int, @Body body: User): Int

    // 좋아요 유무 확인
    @GET("/rest/posts/like/islike/{memberId}/{postId}")
    suspend fun isLikedByUser(@Path("memberId") memberId: Int, @Path("postId") postId: Int): Boolean

    @GET("/rest/posts/like/{memberId}")
    suspend fun getMyLikedPostList(@Query("memberId") memberId: Int): List<LikedPost>

    // 카테고리 분류 리스트 조회
    @GET("/rest/posts/category/{category}")
    suspend fun getPostListByCategory(@Path("category") category: String): List<Post>

    @GET("/rest/posts/top5")
    suspend fun getTop5PostList(): List<Post>

    @GET("/rest/posts/findByTitleContaining")
    suspend fun searchPostList(@Query("title") title: String): List<Post>

    @GET("/rest/Statistics/JobType/{postId}")
    suspend fun getStatistics(@Path("postId") postId: Int): Map<String, Int>
}