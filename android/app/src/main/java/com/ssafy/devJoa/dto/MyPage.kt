package com.ssafy.devJoa.dto

data class MyPage(
    val id: Int = 0,
    val member: User = User(),
    val post: Post = Post()
)