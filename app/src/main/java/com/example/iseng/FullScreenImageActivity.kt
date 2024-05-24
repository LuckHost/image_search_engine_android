package com.example.iseng

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.size.Size
import com.example.iseng.data_model.ImageOutputObject
import com.example.iseng.data_model.ResponseImageObject
import com.example.iseng.ui.theme.IsengTheme
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import kotlin.math.min

class FullScreenImageActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val image = intent.getSerializableExtra("image") as ResponseImageObject
        val passList = intent.getSerializableExtra("images") as? ArrayList<ResponseImageObject>
        val images = passList?.toSnapshotStateList()
        setContent {
            IsengTheme {
                if (images != null) {
                    MainScreen(image, images)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(image: ResponseImageObject,
               images: SnapshotStateList<ResponseImageObject>) {
    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
            .data(image.imageUrl)
            .size(Size.ORIGINAL)
            .build()
    )
    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier
                    .padding(),
                title = {},
                actions = {
                    IconButton(onClick = {

                    }) {
                        Icon(imageVector = Icons.Filled.Search,
                            contentDescription = "Search image")
                    }
                }
            )
        }) {paddingValues ->
        Column(modifier = Modifier
            .padding(paddingValues),
            verticalArrangement = Arrangement.Top,// Stepping back from the topBar
        ) {
            ImageCarousel(items = images)
        }

    }
}

@Composable
fun ImageList(images: SnapshotStateList<ResponseImageObject>) {
    LazyRow(
        modifier = Modifier.fillMaxSize(),
        content = {
            items(images) { image ->
                val painter = rememberAsyncImagePainter(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(image.imageUrl)
                        .size(coil.size.Size.ORIGINAL)
                        .build()
                )
                Box(
                    modifier = Modifier
                        .fillParentMaxWidth()
                        .aspectRatio(1f)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painter,
                        contentDescription = image.title,
                        modifier = Modifier.fillMaxWidth(),
                        contentScale = if (image.imageWidth > image.imageHeight) {
                            ContentScale.FillWidth
                        } else {
                            ContentScale.FillHeight
                        }
                    )
                }
            }
        }
    )
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ImageCarousel(items: List<ResponseImageObject>) {
    val pagerState = rememberPagerState()
    var images by remember { mutableStateOf(items) }

    HorizontalPager(
        count = images.size,
        state = pagerState,
        modifier = Modifier.fillMaxWidth()
    ) { page ->

        val image = images[page]
        val painter = rememberAsyncImagePainter(
            model = ImageRequest.Builder(LocalContext.current)
                .data(image.imageUrl)
                .size(Size.ORIGINAL)
                .networkCachePolicy(CachePolicy.ENABLED)
                .memoryCachePolicy(CachePolicy.ENABLED)
                .diskCachePolicy(CachePolicy.DISABLED)
                .build()
        )
        val painterState = painter.state

        // Убираем изображение, если произошла ошибка загрузки
        if (painterState is AsyncImagePainter.State.Error) {
            images = images.toMutableList().apply { removeAt(page) }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                when (painterState) {
                    is AsyncImagePainter.State.Loading -> {
                        images = images.toMutableList().apply { removeAt(page) }
                    }
                    is AsyncImagePainter.State.Success -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .aspectRatio(1f)
                                .padding(16.dp),
                            contentAlignment = Alignment.TopCenter
                        ) {
                            Column(modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                                verticalArrangement = Arrangement.SpaceBetween) {
                                Text(text = "${page}",
                                    fontFamily = FontFamily.SansSerif,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp,)
                                Image(
                                    painter = painter,
                                    contentDescription = image.title,
                                    modifier = Modifier
                                        .weight(1f),
                                    contentScale = if (image.imageWidth > image.imageHeight) {
                                        ContentScale.FillWidth
                                    } else {
                                        ContentScale.FillHeight
                                    }
                                )
                                Text(text = "Title: ${image.title}",
                                    fontFamily = FontFamily.SansSerif,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp,)
                                Text(text = "Source: ${image.source}",
                                    fontFamily = FontFamily.SansSerif,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,)
                                OpenLinkButton(image.link,
                                    modifier = Modifier.fillMaxWidth())
                            }
                        }
                    }
                    is AsyncImagePainter.State.Error -> {
                        Text(text = "Error loading image")
                    }
                    else -> {}
                }
            }
        }


    }
}

fun <ResponseImageObject> List<ResponseImageObject>.toSnapshotStateList():
        SnapshotStateList<ResponseImageObject> {
    val snapshotStateList = SnapshotStateList<ResponseImageObject>()
    snapshotStateList.addAll(this)
    return snapshotStateList
}