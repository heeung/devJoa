package com.devJoa.api

import com.devJoa.dto.MyPage
import com.devJoa.dto.MyPageToggleKey
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface MyPageService {
    @GET("/rest/mypage/{memberId}")
    suspend fun getMyPageItems(@Path("memberId") memberId: Int): List<MyPage>

    @POST("/rest/mypage/toggle")
    suspend fun myPageToggle(@Body body: MyPageToggleKey): Boolean

    @GET("/rest/mypage/isMyPage/{memberId}/{postId}")
    suspend fun isMyItem(@Path("memberId") memberId: Int, @Path("postId") postId: Int): Boolean
}