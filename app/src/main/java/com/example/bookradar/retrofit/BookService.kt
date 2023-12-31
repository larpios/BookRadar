package com.example.bookradar.retrofit

import com.example.bookradar.model.BookListModel
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface BookService {
    @GET("/v3/search/book")
    suspend fun getBooks(
        @Header("Authorization") authorization: String,
        @Query("query") query: String,
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 50,
    ): BookListModel
}