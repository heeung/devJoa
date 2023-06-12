package com.ssafy.devJoa.dto

data class Product(
    val id: Int = 0,
    val name: String,
    val imageUrl: String,
    val category: String = "KEYBOARD",
) {
    constructor(): this(0, "", "", "")
}