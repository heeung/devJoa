package com.ssafy.devJoa.api

import com.ssafy.devJoa.R
import com.ssafy.devJoa.dto.User
import com.ssafy.devJoa.dto.UserLogin
import retrofit2.Response
import retrofit2.http.*

interface UserService {

    // 사용자 추가
    @POST("/rest/member")
    suspend fun inserUser(@Body body: User): Response<User>

    // 사용자 수정
    @PUT("/rest/member")
    suspend fun modifyUser(@Body body: User): Response<User>

    // 사용자 삭제
    @DELETE("/rest/member/{userId}")
    suspend fun deleteUser(@Path("userId") userId: String): Boolean

    // 깃허브 id에 대한 사용자 정보를 전달.
    @GET("/rest/member/findByGitHubId")
    suspend fun findByGithubId(@Query("gitHubId") gitHubId: String): Response<User>

    // id가 사용중인지 반환.
    @GET("/rest/member/isUsed")
    suspend fun isUsedId(@Query("userId") userId: String): Boolean

    // 로그인 처리 후 쿠키 내려보냄
    @POST("rest/member/login")
    suspend fun login(@Body body: UserLogin): User

    @GET("/rest/member/top5")
    suspend fun getTop5User(): List<User>

    @GET("/rest/member/followCnt/{memberId}")
    suspend fun getFollowCnt(@Path("memberId") memberId: Int): Map<String, Int>

    @GET("/rest/member/findByNicknameLike")
    suspend fun searchUserList(@Query("nickname") nickname: String): List<User>

    @GET("/rest/member/findAll")
    suspend fun getUserList(): List<User>
}