package com.example.iseng

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
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
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.paint
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import kotlin.math.min

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            IsengTheme {
                MainScreen()
            }
        }
    }
}

/**
 * Container of the background and TopPanelWithGrid
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen() {
    Box(
        modifier = with (Modifier){
            fillMaxSize()
                .paint(
                    // Replace with your image id
                    painterResource(id = R.drawable.main_bg),
                    contentScale = ContentScale.FillBounds)

        })
    {
        // Add more views here!
        TopPanelWithGrid()
    }

}


/**
 * The second main function
 * Contains all objects of this activity
 */
@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun TopPanelWithGrid() {
    val context = LocalContext.current
    val items = remember {
        mutableStateListOf<ResponseImageObject>()
    }
    var query by remember { mutableStateOf("Очень интересно Красноярск") }
    val coroutineScope = rememberCoroutineScope()
    var currentPage by remember { mutableIntStateOf(1) }
    var isLoading by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    Scaffold(
        modifier = Modifier
            .pointerInput(Unit) {
            detectTapGestures(onTap = {
                focusManager.clearFocus()
            })},
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ),
                modifier = Modifier
                    .padding(),
                title = {
                    CustomTextField(value = query,
                        onValueChange = {
                            query = it
                        })
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
        }) { paddingValues ->
        Column(modifier = Modifier
            .padding(paddingValues), // Stepping back from the topBar
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if(!isLoading && items.size==0){
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
                        .size(Size.ORIGINAL)
                        .build()
                )
                if(painter.state is AsyncImagePainter.State.Success){
                    ImageItem(image, painter, images)
                }
            }
        }
    )
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
        targetValue = if (painter.state is AsyncImagePainter.State.Success)
            1f else 0f, label = "")
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

/**
 * The stub function
 */
fun onError(s: String) {
    Log.d("OnError", s)
}