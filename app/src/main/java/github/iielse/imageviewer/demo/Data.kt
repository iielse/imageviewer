package github.iielse.imageviewer.demo

import android.os.Handler
import android.os.Looper
import com.github.iielse.imageviewer.core.Photo

data class MyPhoto(val id: Int, val url: String) : Photo {
    override fun id(): Int = id
    override fun url(): String = url
}

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
                numPhoto(key - 7)).filter { it.id >= 0 }.reversed())
    }, 200)
}

private val mainHandler = Handler(Looper.getMainLooper())
private fun numPhoto(value: Int) = MyPhoto(id = value, url = "")