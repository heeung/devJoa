package com.devJoa.dto

data class ReviewComment(
    val id: Int = 0,
    val content: String = "",
    val createDate: String = "",
    val member: User = User(),
    val review: Review = Review()
)