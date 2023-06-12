package com.devJoa.dto

data class InsertReview(
    var reviewId: Int = 0,
    var content: String = "",
    var postId: Int = 0,
    var memberId: Int = 0
) {
    constructor(content: String, postId: Int, memberId: Int): this(0, content, postId, memberId)
    constructor(reviewId: Int, postId: Int, memberId: Int): this(reviewId, "", postId, memberId)
}