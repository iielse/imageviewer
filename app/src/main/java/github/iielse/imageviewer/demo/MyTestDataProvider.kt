package github.iielse.imageviewer.demo

import android.os.Handler
import android.os.Looper
import com.github.iielse.imageviewer.core.DataProvider
import com.github.iielse.imageviewer.core.Photo

class MyTestDataProvider(private val initPhoto: Photo) : DataProvider {
    override fun loadInitial(): List<Photo> {
        return listOf(initPhoto)
    }

    override fun loadAfter(key: Long, callback: (List<Photo>) -> Unit) {
        testFetchAfter(key, callback)
    }

    override fun loadBefore(key: Long, callback: (List<Photo>) -> Unit) {
        testFetchBefore(key, callback)
    }
}



val mainHandler = Handler(Looper.getMainLooper())
private fun numPhoto(value: Long) = MyData(id = value)
fun testFetchAfter(key: Long, callback: (List<MyData>) -> Unit) {
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

fun testFetchBefore(key: Long, callback: (List<MyData>) -> Unit) {
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