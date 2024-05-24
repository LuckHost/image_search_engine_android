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
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.example.iseng.data_model.ImageOutputObject
import com.example.iseng.data_model.ResponseImageObject
import com.example.iseng.ui.theme.IsengTheme
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
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
            ImageCarousel(images = images)
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
fun ImageCarousel(images: List<ResponseImageObject>) {
    val pagerState = rememberPagerState()

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
                .build()
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .aspectRatio(1f)
                .padding(16.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = "${page}",
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,)
                if(painter.state is AsyncImagePainter.State.Loading){
                    GifImage(content = R.drawable.loading_line)
                }
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
                Text(text = "Title: ${image.title}",
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,)
                Text(text = "Source: ${image.source}",
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,)
                OpenLinkButton(image.link)
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