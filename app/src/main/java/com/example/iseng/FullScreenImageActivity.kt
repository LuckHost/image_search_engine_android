package com.example.iseng

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import com.example.iseng.data_model.ImageOutputObject

class FullScreenImageActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val image = intent.getSerializableExtra("KEY_NAME") as ImageOutputObject
        setContent {
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
                    .padding(paddingValues) // Stepping back from the topBar
                ) {
                    Image(bitmap = image.bitmap.asImageBitmap(), contentDescription = " ")
                }

            }
        }
    }
}

@Composable
fun ImageList() {

}