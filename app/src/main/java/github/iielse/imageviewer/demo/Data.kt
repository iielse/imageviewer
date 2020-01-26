package github.iielse.imageviewer.demo

import android.os.Handler
import android.os.Looper
import com.github.iielse.imageviewer.Photo
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

fun fetchAfter(key: Int, callback: (List<Photo>) -> Unit) {
    mainHandler.postDelayed({
        callback(listOf(numPhoto(key + 1),
                numPhoto(key + 2),
                numPhoto(key + 3),
                numPhoto(key + 4),
                numPhoto(key + 5),
                numPhoto(key + 6),
                numPhoto(key + 7)))
    }, 200)
}

fun fetchBefore(key: Int, callback: (List<Photo>) -> Unit) {
    mainHandler.postDelayed({
        callback(listOf(numPhoto(key - 1),
                numPhoto(key - 2),
                numPhoto(key - 3),
                numPhoto(key - 4),
                numPhoto(key - 5),
                numPhoto(key - 6),
                numPhoto(key - 7)).reversed())
    }, 200)
}

private val mainHandler = Handler(Looper.getMainLooper())
private fun numPhoto(value: Int) = Photo(id = value, width = 0, height = 0, url = "")