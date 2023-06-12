package com.ssafy.devJoa.api

import com.ssafy.devJoa.dto.FollowToggleKey
import com.ssafy.devJoa.dto.User
import retrofit2.http.Body
import retrofit2.http.POST

interface FollowService {
    @POST("/rest/follow")
    suspend fun followToggle(@Body bodg: FollowToggleKey): User

    @POST("/rest/follow/isFollowing")
    suspend fun isFollow(@Body body: FollowToggleKey): Boolean
}