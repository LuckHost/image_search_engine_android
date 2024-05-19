package com.example.iseng

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.iseng.data_model.ResponseDataModel
import com.example.iseng.ui.theme.IsengTheme
import com.google.gson.Gson
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
                    ImageObjects(this)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageObjects(context: Context) {
    val state = remember {
        mutableStateListOf("aboba", "никакой абобы")
    }
    
    val items = state
    Column {
        Button(onClick = { makePostRequest("yandex", state, context) }) {
            
        }
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
    
}

fun onError(s: String) {

}

fun makePostRequest(query: String, state: SnapshotStateList<String>, context: Context) {
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
                state.add(responseData.images.first().imageUrl)
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
