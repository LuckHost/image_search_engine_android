package com.example.iseng.data_model

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.snapshots.SnapshotStateList
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import loadImageBitmap
import java.io.Serializable

data class ImageOutputObject(
    val title: String,
    val bitmap: Bitmap,
    val imageWidth: Int,
    var imageHeight: Int,
    val source: String,
    val link: String,
    val position: Int,
) : Serializable

class ListOfImageOutputObjects : Serializable {
    lateinit var list: SnapshotStateList<ImageOutputObject>
}
/**
 * Converts image objects by changing imageUrl to bitmap
 *
 * @param images The list of objects received during the API request
 */
suspend fun convertRespDataToImageObject(
    images: List<ResponseImageObject>,
    context: Context
): MutableList<ImageOutputObject> = coroutineScope {
    val deferredResults = images.map { item ->
        async {
            val loadBitmap = loadImageBitmap(item.imageUrl, context)
            loadBitmap?.let {
                ImageOutputObject(
                    title = item.title,
                    bitmap = it,
                    imageWidth = item.imageWidth,
                    imageHeight = item.imageHeight,
                    source = item.source,
                    link = item.link,
                    position = item.position,
                )
            } ?: run {
                Log.d("Image converter", "Image is null for ${item.imageUrl}")
                null
            }
        }
    }
    deferredResults.awaitAll().filterNotNull().toMutableList()
}
