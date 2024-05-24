package com.example.iseng

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.size.Size
import com.example.iseng.data_model.ResponseImageObject
import com.example.iseng.ui.theme.IsengTheme
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState

class FullScreenImageActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val image = intent.getSerializableExtra("image", ResponseImageObject::class.java)
        val passList = intent.getSerializableExtra("images")
                as? ArrayList<ResponseImageObject>
        val images = passList?.toSnapshotStateList()
        setContent {
            IsengTheme {
                if (images != null && image != null) {
                    MainScreen(image, images) { finish() }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(image: ResponseImageObject,
               images: SnapshotStateList<ResponseImageObject>,
               onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier
                    .padding(),
                title = {},
                navigationIcon = {
                    IconButton(onClick = { onBack()
                    }) {
                        Icon(imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Search image")
                    }
                }
            )
        }) {paddingValues ->
        Column(modifier = Modifier
            .padding(paddingValues),
            verticalArrangement = Arrangement.Top,
        ) {
            ImageCarousel(
                imagePicked = image,
                items = images)
        }

    }
}

/**
 * This feature allows you to show images with a description and a link to the source.
 * Each of them is shown separately. You can scroll through them among themselves.
 */
@OptIn(ExperimentalPagerApi::class)
@Composable
fun ImageCarousel(imagePicked: ResponseImageObject,
                  items: List<ResponseImageObject>) {
    var images by remember { mutableStateOf(items) }
    val firstImageToShowIndex = images.indexOf(imagePicked)
    val pagerState = rememberPagerState(initialPage = firstImageToShowIndex)

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
                                Text(text = "$page",
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