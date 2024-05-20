package com.example.iseng.data_model

import java.security.CodeSource

data class ResponseDataModel(
    val searchParameters: SearchParameters,
    val images: List<ResponseImageObject>,
)

data class SearchParameters(
    val q: String,
    val location: String,
    val num: Int,
)

data class ResponseImageObject(
    val title: String,
    val imageUrl: String,
    val imageWidth: Int,
    val imageHeight: Int,
    val source: String,
    val link: String,
    val position: Int,
)
