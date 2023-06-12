package com.ssafy.devJoa.api

import com.ssafy.devJoa.dto.InsertReview
import com.ssafy.devJoa.dto.Post
import com.ssafy.devJoa.dto.Review
import retrofit2.http.*

interface ReviewService {
    @POST("/rest/review")
    suspend fun insertReview(@Body body: InsertReview): Boolean

    @POST("/rest/review/delete")
    suspend fun deleteReview(@Body body: InsertReview): Boolean

    @PUT("/rest/review/{reviewId}")
    suspend fun modifyReview(@Path("reviewId") reviewId: Int, @Body body: InsertReview): Post
}