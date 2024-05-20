import android.content.Context
import android.graphics.Bitmap
import com.bumptech.glide.Glide
import com.bumptech.glide.request.FutureTarget
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun loadImageBitmap(url: String, context: Context): Bitmap? {
    return withContext(Dispatchers.IO) {
        try {
            val futureTarget: FutureTarget<Bitmap> = Glide.with(context)
                .asBitmap()
                .load(url)
                .submit()

            futureTarget.get()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
