package com.devJoa.api

import okhttp3.MultipartBody
import okhttp3.Response
import retrofit2.http.*

interface FileService {

    // 회원 프로필 이미지 등록
    @Multipart
    @POST("/uploadProfile/{memberId}")
    suspend fun uploadProfile(@Part file: MultipartBody.Part,@Path("memberId")  memberId:Long,)


//    interface UserService {
//        @Multipart
//        @PUT("/api/v1/user")
//        suspend fun updateUserInfo(
//            @Header("AccessToken") accessToken: String,
//            @Header("RefreshToken") refreshToken: String,
//            @Part param: MultipartBody.Part,
//            @Part file: MultipartBody.Part
//        ): Response<UserUpdateResultDto>
//    }


}