package com.example.iseng.data_model

import android.graphics.Bitmap
import android.media.Image

data class ImageOutputObject(
    val title: String,
    val bitmap: Bitmap,
    val imageWidth: Int,
    var imageHeight: Int,
    val source: String,
    val link: String,
    val position: Int,
)
