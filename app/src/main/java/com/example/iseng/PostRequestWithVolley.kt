package com.example.iseng
import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.iseng.data_model.ResponseDataModel
import com.example.iseng.data_model.ResponseImageObject
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

const val API_KEY="b2ae647ad702b58b92d1c25d34841025f0b55217"

/**
 * The function of requests via the Volley library
 */
fun postRequestWithVolley(
    context: Context,
    url: String,
    apiKey: String,
    requestBody: JSONObject,
    onSuccess: (response: JSONObject) -> Unit,
    onError: (error: String) -> Unit
) {
    val requestQueue: RequestQueue = Volley.newRequestQueue(context)
    val jsonObjectRequest = object : JsonObjectRequest(
        Method.POST,
        url,
        requestBody,
        Response.Listener { response ->
            onSuccess(response)
        },
        Response.ErrorListener { error ->
            onError(error.message ?: "Unknown error")
        }
    ) {
        override fun getHeaders(): Map<String, String> {
            val headers = HashMap<String, String>()
            headers["Content-Type"] = "application/json"
            headers["X-API-KEY"] = apiKey
            return headers
        }
    }
    requestQueue.add(jsonObjectRequest)
}

/**
 * Fake request function
 * It is needed in order not to waste API requests
 *
 * Only for development and debugging
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
private fun postRequestWithVolleyImitation(
    context: Context,
    url: String,
    apiKey: String,
    requestBody: JSONObject,
    onSuccess: (response: JSONObject) -> Unit,
    onError: (error: String) -> Unit
) {
    Log.d("postRequestImitation", "input values: $url $apiKey")

    val jsonString = "{\n" +
            "    \"searchParameters\": {\n" +
            "        \"q\": \"apple inc\",\n" +
            "        \"gl\": \"ru\",\n" +
            "        \"hl\": \"ru\",\n" +
            "        \"type\": \"images\",\n" +
            "        \"location\": \"Russia\",\n" +
            "        \"engine\": \"google\",\n" +
            "        \"num\": 10\n" +
            "    },\n" +
            "    \"images\": [\n" +
            "        {\n" +
            "            \"title\": \"A Strategic Analysis of Apple Inc.\",\n" +
            "            \"imageUrl\": \"https://media.licdn.com/dms/image/C4D12AQFNv_KSo_VCwQ/article-cover_image-shrink_600_2000/0/1638142508773?e=2147483647&v=beta&t=SoxCwfG_3-FF8YnKRQNmBv0k0zOPe26PI6-1Nda-GrE\",\n" +
            "            \"imageWidth\": 740,\n" +
            "            \"imageHeight\": 415,\n" +
            "            \"thumbnailUrl\": \"https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTpOBvEgzRNu284eO7Mw-_IKukYnD2CXhGMTs1rjPcJj45uRiyr&s\",\n" +
            "            \"thumbnailWidth\": 300,\n" +
            "            \"thumbnailHeight\": 168,\n" +
            "            \"source\": \"LinkedIn\",\n" +
            "            \"domain\": \"www.linkedin.com\",\n" +
            "            \"link\": \"https://www.linkedin.com/pulse/strategic-analysis-apple-inc-bidemi-ogedengbe\",\n" +
            "            \"googleUrl\": \"https://www.google.com/imgres?imgurl=https%3A%2F%2Fmedia.licdn.com%2Fdms%2Fimage%2FC4D12AQFNv_KSo_VCwQ%2Farticle-cover_image-shrink_600_2000%2F0%2F1638142508773%3Fe%3D2147483647%26v%3Dbeta%26t%3DSoxCwfG_3-FF8YnKRQNmBv0k0zOPe26PI6-1Nda-GrE&tbnid=E8hnCY8LIxTZ3M&imgrefurl=https%3A%2F%2Fwww.linkedin.com%2Fpulse%2Fstrategic-analysis-apple-inc-bidemi-ogedengbe&docid=gP0JwewjX407kM&w=740&h=415&ved=0ahUKEwjppaOF_piGAxV-SDABHVWeAm0QvFcIAigA\",\n" +
            "            \"position\": 1\n" +
            "        },\n" +
            "        {\n" +
            "            \"title\": \"Company Overview of Apple & Intel: Philosophy, Portfolio, etc\",\n" +
            "            \"imageUrl\": \"https://akm-img-a-in.tosshub.com/indiatoday/apple_647_040116105516.jpg?.xccL1E_SxXCNt9K1Jjgg.vKCdo0Mf0u\",\n" +
            "            \"imageWidth\": 647,\n" +
            "            \"imageHeight\": 404,\n" +
            "            \"thumbnailUrl\": \"https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQcLsYkwuPnA4XE5yssV_NEbFf2BXqLXtb3mi-rnC95Htyssxg&s\",\n" +
            "            \"thumbnailWidth\": 284,\n" +
            "            \"thumbnailHeight\": 177,\n" +
            "            \"source\": \"Toppr\",\n" +
            "            \"domain\": \"www.toppr.com\",\n" +
            "            \"link\": \"https://www.toppr.com/guides/commercial-knowledge/business-organizations/company-overview-of-apple-intel/\",\n" +
            "            \"googleUrl\": \"https://www.google.com/imgres?imgurl=https%3A%2F%2Fakm-img-a-in.tosshub.com%2Findiatoday%2Fapple_647_040116105516.jpg%3F.xccL1E_SxXCNt9K1Jjgg.vKCdo0Mf0u&tbnid=oLut_EKUPhhwyM&imgrefurl=https%3A%2F%2Fwww.toppr.com%2Fguides%2Fcommercial-knowledge%2Fbusiness-organizations%2Fcompany-overview-of-apple-intel%2F&docid=bM9CtTTSOxggsM&w=647&h=404&ved=0ahUKEwjppaOF_piGAxV-SDABHVWeAm0QvFcIAygB\",\n" +
            "            \"position\": 2\n" +
            "        },\n" +
            "        {\n" +
            "            \"title\": \"Apple Facts and Statistics (2024) - Investing.com\",\n" +
            "            \"imageUrl\": \"https://academy.education.investing.com/wp-content/uploads/2022/09/Apple-offices.jpg\",\n" +
            "            \"imageWidth\": 1000,\n" +
            "            \"imageHeight\": 667,\n" +
            "            \"thumbnailUrl\": \"https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTnmaODJMQyoRN_ciHqZkr-VRM2IeWKlzAJYt2TX8PEeUHgvOho&s\",\n" +
            "            \"thumbnailWidth\": 275,\n" +
            "            \"thumbnailHeight\": 183,\n" +
            "            \"source\": \"Investing.com\",\n" +
            "            \"domain\": \"www.investing.com\",\n" +
            "            \"link\": \"https://www.investing.com/academy/statistics/apple-facts/\",\n" +
            "            \"googleUrl\": \"https://www.google.com/imgres?imgurl=https%3A%2F%2Facademy.education.investing.com%2Fwp-content%2Fuploads%2F2022%2F09%2FApple-offices.jpg&tbnid=QPkW_VPyf4N7dM&imgrefurl=https%3A%2F%2Fwww.investing.com%2Facademy%2Fstatistics%2Fapple-facts%2F&docid=5W2lLghqFa9chM&w=1000&h=667&ved=0ahUKEwjppaOF_piGAxV-SDABHVWeAm0QvFcIBCgC\",\n" +
            "            \"position\": 3\n" +
            "        },\n" +
            "        {\n" +
            "            \"title\": \"Innovate, Integrate, Dominate: Success Factors of Apple Inc.\",\n" +
            "            \"imageUrl\": \"https://thebrandhopper.com/wp-content/uploads/2023/11/apple-success-story-1024x576.jpg\",\n" +
            "            \"imageWidth\": 1024,\n" +
            "            \"imageHeight\": 576,\n" +
            "            \"thumbnailUrl\": \"https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQMAnrZ5yjVCWU2pqUeQg1DMDfLjYQQSXSPm434nNIbjSl07Sdk&s\",\n" +
            "            \"thumbnailWidth\": 300,\n" +
            "            \"thumbnailHeight\": 168,\n" +
            "            \"source\": \"The Brand Hopper\",\n" +
            "            \"domain\": \"thebrandhopper.com\",\n" +
            "            \"link\": \"https://thebrandhopper.com/2023/11/27/innovate-integrate-dominate-success-factors-of-apple-inc/\",\n" +
            "            \"googleUrl\": \"https://www.google.com/imgres?imgurl=https%3A%2F%2Fthebrandhopper.com%2Fwp-content%2Fuploads%2F2023%2F11%2Fapple-success-story-1024x576.jpg&tbnid=dBWVKtsWMnMJWM&imgrefurl=https%3A%2F%2Fthebrandhopper.com%2F2023%2F11%2F27%2Finnovate-integrate-dominate-success-factors-of-apple-inc%2F&docid=KlOfuwGNeFnNPM&w=1024&h=576&ved=0ahUKEwjppaOF_piGAxV-SDABHVWeAm0QvFcIBSgD\",\n" +
            "            \"position\": 4\n" +
            "        },\n" +
            "        {\n" +
            "            \"title\": \"Apple Inc Company Profile - Apple Inc Overview - GlobalData\",\n" +
            "            \"imageUrl\": \"https://www.globaldata.com/Uploads/Company/1439305/logo.jpg\",\n" +
            "            \"imageWidth\": 199,\n" +
            "            \"imageHeight\": 149,\n" +
            "            \"thumbnailUrl\": \"https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSbbb0g2SXmo2XUZAPNt1ecgbmH5XKC0DExQHi326Jyw1lbQIJx&s\",\n" +
            "            \"thumbnailWidth\": 199,\n" +
            "            \"thumbnailHeight\": 149,\n" +
            "            \"source\": \"GlobalData\",\n" +
            "            \"domain\": \"www.globaldata.com\",\n" +
            "            \"link\": \"https://www.globaldata.com/company-profile/apple-inc/\",\n" +
            "            \"googleUrl\": \"https://www.google.com/imgres?imgurl=https%3A%2F%2Fwww.globaldata.com%2FUploads%2FCompany%2F1439305%2Flogo.jpg&tbnid=jUkIL3b5C2j59M&imgrefurl=https%3A%2F%2Fwww.globaldata.com%2Fcompany-profile%2Fapple-inc%2F&docid=nR6ywIndw6FkoM&w=199&h=149&ved=0ahUKEwjppaOF_piGAxV-SDABHVWeAm0QvFcIBigE\",\n" +
            "            \"position\": 5\n" +
            "        },\n" +
            "        {\n" +
            "            \"title\": \"Apple Fast Facts | CNN\",\n" +
            "            \"imageUrl\": \"https://media.cnn.com/api/v1/images/stellar/prod/180927122050-apple-logo-gfx.jpg?q=w_3000,h_2250,x_0,y_0,c_fill\",\n" +
            "            \"imageWidth\": 3000,\n" +
            "            \"imageHeight\": 2250,\n" +
            "            \"thumbnailUrl\": \"https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQC7CV4Ezdty7yO9Jcsrrinkn0m2FYGUeXdzytZofgS_b5gxig&s\",\n" +
            "            \"thumbnailWidth\": 259,\n" +
            "            \"thumbnailHeight\": 194,\n" +
            "            \"source\": \"CNN\",\n" +
            "            \"domain\": \"www.cnn.com\",\n" +
            "            \"link\": \"https://www.cnn.com/2014/07/01/business/apple-fast-facts/index.html\",\n" +
            "            \"googleUrl\": \"https://www.google.com/imgres?imgurl=https%3A%2F%2Fmedia.cnn.com%2Fapi%2Fv1%2Fimages%2Fstellar%2Fprod%2F180927122050-apple-logo-gfx.jpg%3Fq%3Dw_3000%2Ch_2250%2Cx_0%2Cy_0%2Cc_fill&tbnid=Ic76I5H9E5VYhM&imgrefurl=https%3A%2F%2Fwww.cnn.com%2F2014%2F07%2F01%2Fbusiness%2Fapple-fast-facts%2Findex.html&docid=vRAVHrXY7v7yKM&w=3000&h=2250&ved=0ahUKEwjppaOF_piGAxV-SDABHVWeAm0QvFcIBygF\",\n" +
            "            \"position\": 6\n" +
            "        },\n" +
            "        {\n" +
            "            \"title\": \"Apple, Inc. - Strategic Analysis in Global Context\",\n" +
            "            \"imageUrl\": \"https://media.licdn.com/dms/image/C4E12AQETGRep_JYkuA/article-cover_image-shrink_720_1280/0/1520206550517?e=2147483647&v=beta&t=dk-LW6u9G4WlQr1q8Zcotc2zpHsbAVA0DQrAgeHg6Kk\",\n" +
            "            \"imageWidth\": 1152,\n" +
            "            \"imageHeight\": 720,\n" +
            "            \"thumbnailUrl\": \"https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ_SXLfoyP9FCPXTXsr5Wirpcj8h0tZmnNZtOeipNiTgu0Gbp4w&s\",\n" +
            "            \"thumbnailWidth\": 284,\n" +
            "            \"thumbnailHeight\": 177,\n" +
            "            \"source\": \"LinkedIn\",\n" +
            "            \"domain\": \"www.linkedin.com\",\n" +
            "            \"link\": \"https://www.linkedin.com/pulse/apple-inc-strategic-analysis-global-context-parag-deshpande\",\n" +
            "            \"googleUrl\": \"https://www.google.com/imgres?imgurl=https%3A%2F%2Fmedia.licdn.com%2Fdms%2Fimage%2FC4E12AQETGRep_JYkuA%2Farticle-cover_image-shrink_720_1280%2F0%2F1520206550517%3Fe%3D2147483647%26v%3Dbeta%26t%3Ddk-LW6u9G4WlQr1q8Zcotc2zpHsbAVA0DQrAgeHg6Kk&tbnid=V8CsTo4ZbyAbtM&imgrefurl=https%3A%2F%2Fwww.linkedin.com%2Fpulse%2Fapple-inc-strategic-analysis-global-context-parag-deshpande&docid=lBFHUlv95jRviM&w=1152&h=720&ved=0ahUKEwjppaOF_piGAxV-SDABHVWeAm0QvFcICCgG\",\n" +
            "            \"position\": 7\n" +
            "        },\n" +
            "        {\n" +
            "            \"title\": \"Ð¡Ñ‚Ð¾Ð¸Ð¼Ð¾Ñ\u0081Ñ‚ÑŒ Apple Inc Ð¿Ñ€Ð¸Ð±Ð»Ð¸Ð¶Ð°ÐµÑ‚Ñ\u0081Ñ\u008F Ðº \$1 Ñ‚Ñ€Ð»Ð½ InVenture\",\n" +
            "            \"imageUrl\": \"https://inventure.com.ua/upload/apple_0.jpg\",\n" +
            "            \"imageWidth\": 638,\n" +
            "            \"imageHeight\": 479,\n" +
            "            \"thumbnailUrl\": \"https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTa3u9cry8ZU1LjoXoj7ZXF6taD98ZFjkwYx5vQE9CihwkPPmo&s\",\n" +
            "            \"thumbnailWidth\": 259,\n" +
            "            \"thumbnailHeight\": 194,\n" +
            "            \"source\": \"Inventure\",\n" +
            "            \"domain\": \"inventure.com.ua\",\n" +
            "            \"link\": \"https://inventure.com.ua/news/world/stoimost_apple_inc_priblizhaetsya_k_dollar1_trln\",\n" +
            "            \"googleUrl\": \"https://www.google.com/imgres?imgurl=https%3A%2F%2Finventure.com.ua%2Fupload%2Fapple_0.jpg&tbnid=oABNOT2AWv2LRM&imgrefurl=https%3A%2F%2Finventure.com.ua%2Fnews%2Fworld%2Fstoimost_apple_inc_priblizhaetsya_k_dollar1_trln&docid=UvOa1OwUtcuQ-M&w=638&h=479&ved=0ahUKEwjppaOF_piGAxV-SDABHVWeAm0QvFcICSgH\",\n" +
            "            \"position\": 8\n" +
            "        },\n" +
            "        {\n" +
            "            \"title\": \"Ð˜Ñ\u0081Ñ‚Ð¾Ñ€Ð¸Ñ\u008F Apple | Ð˜Ñ\u0081Ñ‚Ð¾Ñ€Ð¸Ð¸ Ð±Ñ€ÐµÐ½Ð´Ð¾Ð²\",\n" +
            "            \"imageUrl\": \"https://quokka.media/wp-content/uploads/2020/05/telefinchik1.jpg\",\n" +
            "            \"imageWidth\": 1280,\n" +
            "            \"imageHeight\": 683,\n" +
            "            \"thumbnailUrl\": \"https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSb-QmV5NFnmCb7Qh5fZke4zEn4ojTpNqyUl8JL8UrUhYgrnA4&s\",\n" +
            "            \"thumbnailWidth\": 308,\n" +
            "            \"thumbnailHeight\": 164,\n" +
            "            \"source\": \"Quokka Media\",\n" +
            "            \"domain\": \"quokka.media\",\n" +
            "            \"link\": \"https://quokka.media/istorii-brendov/apple/\",\n" +
            "            \"googleUrl\": \"https://www.google.com/imgres?imgurl=https%3A%2F%2Fquokka.media%2Fwp-content%2Fuploads%2F2020%2F05%2Ftelefinchik1.jpg&tbnid=t-SSTIEWwR9koM&imgrefurl=https%3A%2F%2Fquokka.media%2Fistorii-brendov%2Fapple%2F&docid=sSc2OCk4jRYiMM&w=1280&h=683&ved=0ahUKEwjppaOF_piGAxV-SDABHVWeAm0QvFcICigI\",\n" +
            "            \"position\": 9\n" +
            "        },\n" +
            "        {\n" +
            "            \"title\": \"Apple Inc. - Wikipedia\",\n" +
            "            \"imageUrl\": \"https://upload.wikimedia.org/wikipedia/commons/thumb/5/5a/Aerial_view_of_Apple_Park_dllu.jpg/250px-Aerial_view_of_Apple_Park_dllu.jpg\",\n" +
            "            \"imageWidth\": 250,\n" +
            "            \"imageHeight\": 167,\n" +
            "            \"thumbnailUrl\": \"https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTr-hiVV8lrDFEbnf8rj0bwIqVnkYYX_5KvSjZvO2qkPtJS-Qg&s\",\n" +
            "            \"thumbnailWidth\": 250,\n" +
            "            \"thumbnailHeight\": 167,\n" +
            "            \"source\": \"Wikipedia\",\n" +
            "            \"domain\": \"en.wikipedia.org\",\n" +
            "            \"link\": \"https://en.wikipedia.org/wiki/Apple_Inc.\",\n" +
            "            \"googleUrl\": \"https://www.google.com/imgres?imgurl=https%3A%2F%2Fupload.wikimedia.org%2Fwikipedia%2Fcommons%2Fthumb%2F5%2F5a%2FAerial_view_of_Apple_Park_dllu.jpg%2F250px-Aerial_view_of_Apple_Park_dllu.jpg&tbnid=wCTCwGhx-NGTRM&imgrefurl=https%3A%2F%2Fen.wikipedia.org%2Fwiki%2FApple_Inc.&docid=gk5eHYqeOTZu-M&w=250&h=167&ved=0ahUKEwjppaOF_piGAxV-SDABHVWeAm0QvFcICygJ\",\n" +
            "            \"position\": 10\n" +
            "        }\n" +
            "    ]\n" +
            "}"

    val jsonObject = JSONObject(jsonString)
    onSuccess(jsonObject)

}

/**
 * A post request function that adds the result
 * that was received from the server to the sheet given to it
 *
 * @param query Request name
 * @param page The number of the desired page
 * @param state The list to which new items will be added
 * @param isLoading A Boolean value that makes it clear
 * whether the download is currently in progress
 */

fun makePostRequest(query: String,
                    page: Int,
                    state: SnapshotStateList<ResponseImageObject>,
                    context: Context,
                    isLoading: (Boolean) -> Unit) {
    val url = "https://google.serper.dev/images"
    val apiKey = API_KEY
    val requestBody = JSONObject()
    requestBody.put("q", query)
    requestBody.put("location", "Russia")
    requestBody.put("gl", "ru")
    requestBody.put("hl", "ru")
    requestBody.put("num", "15")
    requestBody.put("page", page.toString())
    isLoading(true)

    postRequestWithVolley(
        context = context,
        url = url,
        apiKey = apiKey,
        requestBody = requestBody,
        onSuccess = { response ->
            try {
                val gson = Gson()
                val responseData: ResponseDataModel =
                    gson.fromJson(response.toString(), ResponseDataModel::class.java)
                CoroutineScope(Dispatchers.IO).launch {
                    state.addAll(responseData.images)
                    isLoading(false)
                }
            } catch (e: Exception) {
                onError(e.message ?: "JSON parsing error")
                isLoading(false)
            }
        },
        onError = { error ->
            Log.d("makePostRequest", ": $error")
            isLoading(false)
        }
    )
}

