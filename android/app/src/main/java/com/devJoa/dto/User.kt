package com.devJoa.dto

import java.sql.Timestamp

data class User(
    val userId: String,
    var password: String,
    var nickname: String,
    var jobtype: String,
    var githubId: String = "",

    val id: Int = 0,
    var careerLevel: String = "NONE",
    val createDate: String = "",
    val followerCount: Int = 0,
    val followingCount: Int = 0,
    var imageUrl: String = "",
    val roletype: String = "USER"
): java.io.Serializable {
    constructor(): this("", "", "", "")
    constructor(userId: String, nickname: String): this(userId, "", nickname, "")
    constructor(userId: String, password: String, nickname: String, jobtype: String): this(userId, password, nickname, jobtype, "")
    constructor(userId: String, password: String, nickname: String, jobtype: String, githubId: String): this(userId, password, nickname, jobtype, githubId, 0)
}