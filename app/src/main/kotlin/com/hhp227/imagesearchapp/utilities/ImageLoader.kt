package com.hhp227.imagesearchapp.utilities

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.LruCache
import kotlinx.coroutines.*
import java.net.URL

object ImageLoader {
    private const val NO_IMAGE = "https://search1.daumcdn.net/search/statics/common/pi/noimg/img_size_l.png"

    private val imageCache = LruCache<String, Bitmap>(80 * 1024 * 1024)

    fun loadImage(url: String, completed: (Bitmap?) -> Unit) {
        if (url.isEmpty()) {
            completed(null)
            return
        }
        if (imageCache[url] != null) {
            completed(imageCache[url])
            return
        }
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val bitmap = BitmapFactory.decodeStream(URL(url).openStream())
                imageCache.put(url, bitmap)

                withContext(Dispatchers.Main) {
                    completed(bitmap)
                }
            } catch (e: Exception) {
                val bitmap = BitmapFactory.decodeStream(URL(NO_IMAGE).openStream())
                imageCache.put(url, bitmap)

                withContext(Dispatchers.Main) {
                    completed(bitmap)
                }
            }
        }
    }
}