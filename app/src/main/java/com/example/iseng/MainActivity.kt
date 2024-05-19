package com.example.iseng

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.iseng.ui.theme.IsengTheme

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
                    ImageObjects(listOf("a", "b", "c", "a", "b", "c","a", "b", "c","a", "b",
                        "c","a", "b", "c","a", "b", "c",
                        "c","a", "b", "c","a", "b", "c",
                        "c","a", "b", "c","a", "b", "c",
                        "c","a", "b", "c","a", "b", "c",))
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!!!!",
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageObjects(items: List<String>) {
    LazyColumn {
        itemsIndexed(items.chunked(2)) { _, rowItems ->
            Row(

                modifier = Modifier
                    .padding(4.dp)
                    .fillMaxWidth()

            ) {
                Card(
                    modifier = Modifier
                        .padding(10.dp),
                    onClick = { /*TODO*/ },
                    elevation = CardDefaults.cardElevation(
                        defaultElevation  = 5.dp)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.5f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = rowItems.first())
                    }
                }
                Card(
                    modifier = Modifier
                        .padding(10.dp),
                    onClick = { /*TODO*/ },
                    elevation = CardDefaults.cardElevation(
                        defaultElevation  = 5.dp)) {
                    if (rowItems.size < 2) {
                        Box(modifier = Modifier.weight(1f)) {}
                    }
                    else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = rowItems.last())
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    IsengTheme {
        Greeting("Android")
    }
}