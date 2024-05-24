import android.content.Context
import android.graphics.Bitmap
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.FutureTarget
import com.bumptech.glide.request.RequestOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

/**
 * Not used at the moment
 *
 * Returns bitmap or null by url link
 *
 * @param url link of an image
 *
 */
suspend fun loadImageBitmap(url: String, context: Context): Bitmap? {
    return withContext(Dispatchers.IO) {
        try {
            val futureTarget: FutureTarget<Bitmap> = Glide.with(context)
                .asBitmap()
                .load(url)
                .apply(RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .timeout(1500)) // Увеличен тайм-аут
                .submit()
            val bitmap = futureTarget.get(3, TimeUnit.SECONDS) // Добавлено время ожидания
            // Очистка после использования
            bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}