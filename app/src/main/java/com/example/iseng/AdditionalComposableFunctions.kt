package com.example.iseng

import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import coil.size.Size
import com.example.iseng.data_model.ResponseImageObject

/**
 * Creates an Image with a gif animation
 *
 * @param content Link for a .gif file
 * (*R.drawable.funny_cat* for example)
 */
@Composable
fun GifImage(content: Int) {
    val context = LocalContext.current
    val imageLoader = ImageLoader.Builder(context)
        .components {
            if (Build.VERSION.SDK_INT >= 28) {
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
fun OpenLinkButton(url: String, modifier: Modifier) {
    val context = LocalContext.current
    val openLink: (url: String) -> Unit = {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(url)
        }
        context.startActivity(intent)
    }
    Button(modifier = modifier,
        onClick = { openLink(url) }) {
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
                OpenLinkButton(image.link, modifier = Modifier)
            }
        }

    }
}

/**
 * It looks like a regular TextField, but with changed colors
 */
@Composable
fun CustomTextField(value: String, onValueChange: (String) -> Unit) {
    TextField(
        value = value,
        maxLines = 1,
        onValueChange = onValueChange,
        label = { Text("Enter the query here",
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,) },
        colors = TextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.Gray,

            focusedLabelColor = Color.Gray,
            unfocusedLabelColor = Color.LightGray,


            focusedIndicatorColor = Color.Gray,
            unfocusedIndicatorColor = Color.LightGray,

            disabledTextColor = Color.Gray,

            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,

            cursorColor = Color.White,


            disabledIndicatorColor = Color.Transparent,
            errorIndicatorColor = Color.Red,
        ),
        textStyle = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.W600,
            fontSize = 18.sp,
        )
    )
}