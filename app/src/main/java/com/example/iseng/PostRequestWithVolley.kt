package com.example.iseng
import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

fun postRequestWithVolley(
    context: Context,
    url: String,
    apiKey: String,
    requestBody: JSONObject,
    onSuccess: (response: JSONObject) -> Unit,
    onError: (error: String) -> Unit
) {
    // Создание очереди запросов
    val requestQueue: RequestQueue = Volley.newRequestQueue(context)

    // Создание запроса
    val jsonObjectRequest = object : JsonObjectRequest(
        Request.Method.POST,
        url,
        requestBody,
        Response.Listener { response ->
            // Обработка успешного ответа
            onSuccess(response)
        },
        Response.ErrorListener { error ->
            // Обработка ошибки
            onError(error.message ?: "Unknown error")
        }
    ) {
        // Добавление заголовков
        override fun getHeaders(): Map<String, String> {
            val headers = HashMap<String, String>()
            headers["Content-Type"] = "application/json"
            headers["X-API-KEY"] = apiKey
            return headers
        }
    }

    // Добавление запроса в очередь
    requestQueue.add(jsonObjectRequest)
}
