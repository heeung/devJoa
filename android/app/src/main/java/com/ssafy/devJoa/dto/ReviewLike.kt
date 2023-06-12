package com.ssafy.devJoa.dto

data class ReviewLike(
    var id: Int = 0,
    var member: User = User(),
    var review: Review = Review()
)