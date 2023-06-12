package com.ssafy.devJoa.dto

import java.sql.Timestamp

data class User(
    val userId: String,
    var password: String,
    var nickname: String,
    var jobtype: String,

    val id: Int = 0,
    var careerLevel: String = "NONE",
    val createDate: String = "",
    val followerCount: Int = 0,
    val followingCount: Int = 0,
    var githubId: String = "",
    var imageUrl: String = "",
    val roletype: String = "USER"
): java.io.Serializable {
    constructor(): this("", "", "", "")
    constructor(userId: String, nickname: String): this(userId, "", nickname, "")
    constructor(userId: String, password: String, nickname: String, jobtype: String): this(userId, password, nickname, jobtype, 0)
}