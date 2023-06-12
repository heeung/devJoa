package com.ssafy.devJoa.api

import com.ssafy.devJoa.dto.Product
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ProductService {
    // 상품 단건조회
    @GET("/rest/product/{name}")
    suspend fun getProduct(@Path("name") name: String): Product

    // 상품 전체조회
    @GET("/rest/product/all")
    suspend fun getProductList(): List<Product>

    // 카테고리로 상품 조회
    @GET("/rest/product/findByCategory")
    suspend fun getProductListWithCategory(@Query ("category") category: String) : List<Product>

    // 상품 이름으로 조회
    @GET("/rest/product/findByNameContaining")
    suspend fun search(@Query ("string") string: String): List<Product>
}