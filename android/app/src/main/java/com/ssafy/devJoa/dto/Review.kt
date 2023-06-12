package com.ssafy.devJoa.dto

data class Review(
    var id: Int = 0,
    var member: User = User(),
    var content: String = "",
    var post: Post = Post(),
    var commentList: List<ReviewComment> = listOf(),
    var likeCnt: Int = 0,
    var createDate: String = "",
    var commentCnt: Int = 0,
    var reviewLike: List<ReviewLike> = listOf(),
    var rating: Int = 0
)