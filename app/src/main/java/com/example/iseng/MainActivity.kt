package com.example.iseng

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.example.iseng.data_model.ResponseImageObject
import com.example.iseng.ui.theme.IsengTheme
import kotlinx.coroutines.launch
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Button
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.ImageLoader
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import coil.size.Size
import kotlin.math.min

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            IsengTheme {
                MainScreen(context = this)
            }
        }
    }
}

/**
 * Container of the main objects of this activity
 */
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
                modifier = Modifier
                    .padding(),
                title = {
                    OutlinedTextField(
                        value = query,
                        onValueChange = { query = it },
                        label = { Text("Enter the query",
                                fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,) },
                        maxLines = 1,
                        modifier = Modifier.padding(20.dp),
                        textStyle = TextStyle(
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.W400,
                            fontSize = 16.sp,
                        )
                    )
                },
                actions = {
                    IconButton(onClick = {
                        items.clear()
                        makePostRequest(query, currentPage,
                            items, context, isLoading = { isLoading = it })
                    }) {
                        Icon(imageVector = Icons.Filled.Search,
                            contentDescription = "Search image")
                    }
                }
            )
        }) {paddingValues ->
        Column(modifier = Modifier
            .padding(paddingValues), // Stepping back from the topBar
            horizontalAlignment = Alignment.CenterHorizontally
            ) {
            if(!isLoading&&items.size==0){
                GifImage(content = R.drawable.please_cat)
                Text(text = "Please enter the query",
                    textAlign = TextAlign.Center,
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.W600,
                    fontSize = 16.sp,)
            }
            GridOfImages(images = items, loadMore = {
                coroutineScope.launch {
                    if (!isLoading) {
                        makePostRequest(query, currentPage,
                            items, context, isLoading = { isLoading = it })
                        currentPage++
                        Log.d("loadMore", "More pictures loading is started")
                    }
                }
            })
            if(isLoading) {
                GifImage(R.drawable.loading_line)
            }
        }

    }
}

@Composable
fun TopBar() {

}

/**
 * Grid of *ImageItem*
 *
 * @param images the list of ImageOutputObject that will be displayed
 * @param loadMore Unit that will be called when the grid comes to the end
 */
@Composable
fun GridOfImages(images: SnapshotStateList<ResponseImageObject>,
                 loadMore: () -> Unit) {
    val staggeredGridState = rememberLazyStaggeredGridState()

    LaunchedEffect(staggeredGridState) {
        snapshotFlow { staggeredGridState.layoutInfo.visibleItemsInfo }
            .collect { visibleItemsInfo ->
                if (visibleItemsInfo.isNotEmpty()) {
                    val scrollIndex = visibleItemsInfo.maxOf { it.index }
                    if(images.size - scrollIndex - 8 <= 5){
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
                val painter = rememberAsyncImagePainter(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(image.imageUrl)
                        .size(coil.size.Size.ORIGINAL)
                        .build()
                )
                if(painter.state is AsyncImagePainter.State.Success){
                    ImageItem(image, painter, images)
                } else if (painter.state is AsyncImagePainter.State.Error){
                    images.remove(image)
                }
            }
        }
    )
}

/**
 * Creates an Image with a gif animation
 *
 * @param content Link for a .gif file
 * (*R.drawable.funny_cat* for example)
 */
@Composable
fun GifImage(
    content: Int,
) {
    val context = LocalContext.current
    val imageLoader = ImageLoader.Builder(context)
        .components {
            if (SDK_INT >= 28) {
                add(ImageDecoderDecoder.Factory())
            } else {
                add(GifDecoder.Factory())
            }
        }
        .build()
    Image(
        painter = rememberAsyncImagePainter(
            ImageRequest.Builder(context).data(data = content)
                .apply(block = {
                size(Size.ORIGINAL)
            }).build(), imageLoader = imageLoader
        ),
        contentDescription = null,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
    )
}

@Composable
fun OpenLinkButton(url: String) {
    val context = LocalContext.current
    val openLink: (url: String) -> Unit = {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(url)
        }
        context.startActivity(intent)
    }
    Button(onClick = { openLink(url) }) {
        Text(text = "Open Link")
    }
}

/**
 * Creates a dialog with an image and description
 *
 * @param image An ImageOutputObject object containing information about the image
 * @param state the state that contains the bool value.
 * True - the dialog is shown, False - it is not shown
 */
@Composable
fun DialogScreen(image: ResponseImageObject, state: MutableState<Boolean>) {
    Dialog(
        onDismissRequest = { state.value = false },
        properties = DialogProperties(dismissOnBackPress = true,
            dismissOnClickOutside = false, usePlatformDefaultWidth = false)
    ){
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(20.dp)
        ) {
            Column {
                Image(
                    painter = rememberAsyncImagePainter(image.imageUrl),
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    contentDescription = image.title,
                    contentScale = if (image.imageWidth > image.imageHeight) {
                        ContentScale.FillWidth
                    } else {
                        ContentScale.FillHeight
                    }
                )
                Text(text = image.title)
                OpenLinkButton(image.link)
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
fun ImageItem(image: ResponseImageObject,
              painter: AsyncImagePainter,
              images: SnapshotStateList<ResponseImageObject>) {
    val showDialog = remember { mutableStateOf(false) }
    val transition by animateFloatAsState(
        targetValue = if (painter.state is AsyncImagePainter.State.Success) 1f else 0f, label = ""
    )
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .padding(8.dp)
            .width(40.dp),
        onClick = {
            val intent = Intent(context, FullScreenImageActivity::class.java)
            intent.putExtra("image", image)
            val passList: List<ResponseImageObject> = images.toList()
            intent.putExtra("images", ArrayList(passList))

            context.startActivity(intent) },
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
    ) {
        Box(
            modifier = Modifier,
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painter,
                modifier = Modifier
                    .scale(0.8f + 0.2f * transition)
                    .graphicsLayer { rotationX = (1f - transition) * 5f }
                    .alpha(min(1f, transition / 0.2f))
                    .fillMaxSize(),
                contentDescription = image.title,
                contentScale = if (image.imageWidth > image.imageHeight) {
                    ContentScale.FillWidth
                } else {
                    ContentScale.FillHeight
                }
            )
        }
    }

    if (showDialog.value) {
        DialogScreen(image = image, showDialog)
    }
}


@Composable
fun ExcludeNonDownloadableImages(images: List<ResponseImageObject>) {
    images.map { item ->
        val painter = rememberAsyncImagePainter("https://www.example.com/image.jpg")
        val imageLoadState = remember{ derivedStateOf{painter.state} }
        when(imageLoadState.value) {
            is AsyncImagePainter.State.Loading -> {}
            is AsyncImagePainter.State.Success -> {
                painter.let { item }
            }
            is AsyncImagePainter.State.Error -> {
                run { null }
            }
            AsyncImagePainter.State.Empty -> run { null }
        }
    }
    images.filterNotNull()
}

/**
 * The stub function
 */
fun onError(s: String) {
    Log.d("OnError", s)
}