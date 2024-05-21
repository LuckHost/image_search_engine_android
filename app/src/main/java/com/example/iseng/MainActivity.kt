package com.example.iseng

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.example.iseng.data_model.ImageOutputObject
import com.example.iseng.data_model.ResponseDataModel
import com.example.iseng.data_model.ResponseImageObject
import com.example.iseng.ui.theme.IsengTheme
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import loadImageBitmap
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.items
import coil.compose.AsyncImage
import org.json.JSONObject

const val API_KEY="b2ae647ad702b58b92d1c25d34841025f0b55217"
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            IsengTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(context = this)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(context: Context) {
    val items = remember {
        mutableStateListOf<ImageOutputObject>()
    }

    var query by remember { mutableStateOf("Очень интересно Красноярск") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    OutlinedTextField(
                        value = query,
                        onValueChange = { query = it },
                        label = { Text("Enter the query") },
                        maxLines = 2,
                        modifier = Modifier.padding(20.dp),
                    )
                        },
                actions = {
                    IconButton(onClick = { makePostRequest(query, items, context) }) {
                        Icon(imageVector = Icons.Filled.Search,
                            contentDescription = "Search image")
                    }
                }
                )
        }) {
        ImageObjects(context = context, items)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageObjects(context: Context,
                 images: SnapshotStateList<ImageOutputObject>) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Adaptive(200.dp),
        verticalItemSpacing = 4.dp,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        content = {
            items(images) { image ->
                ImageItem(image)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageItem(image: ImageOutputObject) {
    Card(
        modifier = Modifier
            .padding(8.dp),
        onClick = { /*TODO*/ },
        elevation = CardDefaults.cardElevation(
            defaultElevation  = 5.dp)) {
        Box(
            modifier = Modifier,
            contentAlignment = Alignment.Center
        ) {
            Image(bitmap = image.bitmap.asImageBitmap(),
                modifier = Modifier.fillMaxSize().wrapContentHeight(),
                contentDescription = image.title,
                contentScale = ContentScale.Crop)

        }
    }
}

fun makePostRequest(query: String,
                    state: SnapshotStateList<ImageOutputObject>, context: Context) {
    val url = "https://google.serper.dev/images"
    val apiKey = "b2ae647ad702b58b92d1c25d34841025f0b55217"
    val requestBody = JSONObject()
    requestBody.put("q", query)
    requestBody.put("location", "Russia")
    requestBody.put("gl", "ru")
    requestBody.put("hl", "ru")

    postRequestWithVolley(
        context = context,
        url = url,
        apiKey = apiKey,
        requestBody = requestBody,
        onSuccess = { response ->
            // Обработка успешного ответа
            try {
                val gson = Gson()
                val responseData: ResponseDataModel =
                    gson.fromJson(response.toString(), ResponseDataModel::class.java)
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val images = convertRespDataToImageObject(responseData.images, context)
                        withContext(Dispatchers.Main) {
                            state.addAll(images)
                        }
                    } catch (e: Exception) {
                        onError(e.message ?:
                        "Error converting response data to image objects")
                    }
                }
            } catch (e: Exception) {
                onError(e.message ?: "JSON parsing error")
            }
            Log.d("makePostRequest", ": $response")
        },
        onError = { error ->
            // Обработка ошибки
            Log.d("makePostRequest", ": $error")
        }
    )
}

fun onError(s: String) {
    Log.d("OnError", s)
}

suspend fun convertRespDataToImageObject(images: List<ResponseImageObject>,
                                         context: Context):
        MutableList<ImageOutputObject> {
    val output: MutableList<ImageOutputObject> = mutableListOf()
    for(item in images) {
        val loadBitmap = loadImageBitmap(item.imageUrl, context)
        loadBitmap?.let {
            val newImageObject = ImageOutputObject(
                title = item.title,
                bitmap = loadBitmap,
                imageWidth = item.imageWidth,
                imageHeight = item.imageHeight,
                source = item.source,
                link = item.link,
                position = item.position,
            )
            output.add(newImageObject)
        } ?: run {
            // Этот блок выполнится, если myObject null
            Log.d("Image converter", "Image is null")
        }
    }
    return output
}
