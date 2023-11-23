package com.example.bookradar.retrofit.model

data class DocumentModel(
    val title: String,
    val contents: String,
    val url: String,
    val isbn: String,
    val datetime: String,
    val authors: List<String>,
    val publisher: String,
    val translators: List<String>,
    val price: Int,
    val sale_price: Int,
    val thumbnail: String
)
