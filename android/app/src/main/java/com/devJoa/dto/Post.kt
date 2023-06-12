package com.devJoa.dto

data class Post(
    val id: Int = 0,
    val title: String = "",
    val commentCnt: Int = 0,
    val product: Product = Product(),
    val content: String = "",
    val imgUrl: String = "",
    val likeCnt: Int = 0,
    val memberId: Int = 0,
    val productId: Int = 0,
    var reivewList: List<Review> = listOf(),
    val rating: Int = 0
) {

}