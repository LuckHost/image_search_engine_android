package com.example.iseng

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
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
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material.TextButton
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberCompositionContext
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import org.json.JSONObject
import java.util.UUID

const val API_KEY="b2ae647ad702b58b92d1c25d34841025f0b55217______"


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
        mutableStateListOf<ResponseImageObject>()
    }
    var query by remember { mutableStateOf("Очень интересно Красноярск") }
    val coroutineScope = rememberCoroutineScope()
    var currentPage by remember { mutableIntStateOf(1) }
    var isLoading by remember { mutableStateOf(false) }

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
                    IconButton(onClick = {
                        currentPage = 2
                        makePostRequest(query, currentPage,
                            items, context, isLoading = { isLoading = it })
                    }) {
                        Icon(imageVector = Icons.Filled.Search,
                            contentDescription = "Search image")
                    }
                }
                )
        }) {

        ImageObjects(images = items, loadMore = {
            coroutineScope.launch {
                if (!isLoading) {
                    Log.d("Pagination load more", "Loading new images started")
                    makePostRequest(query, currentPage,
                        items, context, isLoading = { isLoading = it })
                    currentPage++
                }
            }
        })
    }
}

/**
 * Grid of *ImageItem*
 *
 * @param images a list of ImageOutputObject to be displayed
 * @param loadMore Unit, that will be called, when the grid comes to an end
 */
@Composable
fun ImageObjects(images: SnapshotStateList<ResponseImageObject>,
                 loadMore: () -> Unit) {
    val staggeredGridState = rememberLazyStaggeredGridState()

    LaunchedEffect(staggeredGridState) {
        snapshotFlow { staggeredGridState.layoutInfo.visibleItemsInfo }
            .collect { visibleItemsInfo ->
                if (visibleItemsInfo.isNotEmpty()) {
                    val scrollIndex = visibleItemsInfo.minOf { it.index }
                    if(images.size - scrollIndex - 5 <= 5){
                        Log.d("Pagination load more",
                            "first visible index $scrollIndex "+
                                    "list size ${images.size}")
                        loadMore()
                    }
                }
            }
    }
    LazyVerticalStaggeredGrid(
        state = staggeredGridState,
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
fun FullScreen(image: ResponseImageObject,
               state: MutableState<Boolean>,
               painter: Painter) {
    Dialog(
        onDismissRequest = { state.value = false },
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = false,
            usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(20.dp)
        ) {
            Column {
                Image(
                    painter = painter,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight() // Ограничиваем размер изображения
                        .padding(16.dp),
                    contentDescription = image.title,
                    contentScale = ContentScale.FillWidth
                )
                Text(text = image.title)
                Text(text = image.source)
            }
        }
    }
}

/**
 * A card with a picture
 *
 * @param image an object of the olaf class,
 * which is then converted to Image()
 *
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageItem(image: ResponseImageObject) {
    val showDialog = remember { mutableStateOf(false) }

    val painter = rememberAsyncImagePainter(
        "https://kartinki.pics/uploads/posts/2021-07/1625792456_11-kartinkin-" +
                "com-p-anime-dlinnie-volosi-anime-krasivo-12.jpg"
    )
    // Добавим состояние для отслеживания загрузки изображения
    val imageLoadState = remember  { derivedStateOf{painter.state} }

    Card(
        modifier = Modifier
            .padding(8.dp),
        onClick = { showDialog.value = true },
        elevation = CardDefaults.cardElevation(
            defaultElevation  = 5.dp)) {
        Box(
            modifier = Modifier,
            contentAlignment = Alignment.Center
        ) {

            when(imageLoadState.value) {
                is AsyncImagePainter.State.Loading -> {
                    // Отображаем индикатор загрузки, пока изображение загружается
                    Text(text = "Loading")
                }
                is AsyncImagePainter.State.Success -> {
                    // Когда изображение загружено успешно, отображаем его
                    AsyncImage(model = image,
                        modifier = Modifier
                            .fillMaxSize()
                            .wrapContentHeight(),
                        contentDescription = image.title,
                        contentScale = ContentScale.Crop
                    )
                }
                is AsyncImagePainter.State.Error -> {
                    // Если возникла ошибка при загрузке изображения,
                    // показываем сообщение об ошибке
                    Text(text = "Ошибка загрузки изображения")
                }
                AsyncImagePainter.State.Empty -> TODO()
            }
        }
    }
    if (showDialog.value) {
        FullScreen(image = image, showDialog, painter)
    }
}


fun makePostRequest(query: String,
                    page: Int,
                    state: SnapshotStateList<ResponseImageObject>,
                    context: Context,
                    isLoading: (Boolean) -> Unit) {
    val url = "https://google.serper.dev/images"
    val apiKey = API_KEY
    val requestBody = JSONObject()
    requestBody.put("q", query)
    requestBody.put("location", "Russia")
    requestBody.put("gl", "ru")
    requestBody.put("hl", "ru")
    requestBody.put("num", "10")
    requestBody.put("page", page.toString())

    isLoading(true)

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
                        val images =
                            convertRespDataToImageObject(responseData.images, context)
                        withContext(Dispatchers.Main) {
                            state.addAll(responseData.images)
                            isLoading(false)
                        }
                    } catch (e: Exception) {
                        onError(e.message ?:
                        "Error converting response data to image objects")
                        isLoading(false)
                    }
                }
            } catch (e: Exception) {
                onError(e.message ?: "JSON parsing error")
                isLoading(false)
            }
            Log.d("makePostRequest", ": $response")
        },
        onError = { error ->
            // Обработка ошибки
            Log.d("makePostRequest", ": $error")
            isLoading(false)
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
