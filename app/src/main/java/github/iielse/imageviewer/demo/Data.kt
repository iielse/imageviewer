package github.iielse.imageviewer.demo

import android.graphics.*
import android.os.Handler
import android.os.Looper
import com.github.iielse.imageviewer.core.Photo
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlin.math.abs
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

private fun desc(num: Long): String {
    return when (num % 5) {
        0L -> "desc 1111 desc 1111 desc 1111 desc 1111 desc 1111 desc 1111 desc 1111 desc 1111 desc 1111 desc 1111 desc 1111 "
        1L -> "desc 2222 desc 2222 desc 2222 desc 2222 desc 2222 desc 2222 desc 2222 desc 2222 desc 2222 desc 2222 desc 2222 "
        2L -> "desc 3333 desc 3333 desc 3333 desc 3333 desc 3333 desc 3333 desc 3333 desc 3333 desc 3333 desc 3333 desc 3333 "
        3L -> "desc 4444 desc 4444 desc 4444 desc 4444 desc 4444 desc 4444 desc 4444 desc 4444 desc 4444 desc 4444 desc 4444 "
        else -> "desc 5555 desc 5555 desc 5555 desc 5555 desc 5555 desc 5555 desc 5555 desc 5555 desc 5555 desc 5555 desc 5555 "
    }
}

data class MyViewerData(val id: Long, val url: String = "", val desc: String = desc(id)) : Photo {
    override fun id(): Long = id
    override fun subsampling() = id % 5 == 0L
}

private val mainHandler = Handler(Looper.getMainLooper())
private val colors: List<Int> = listOf(
        0xfff2fefe.toInt(),
        0xffece9f6.toInt(),
        0xffc5c1d5.toInt(),
        0xffe8dcea.toInt(),
        0xfffbe8eb.toInt(),
        0xfffff6f8.toInt()
)
private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
    textSize = 60f
    color = Color.BLACK
}

private val paint2 = Paint(Paint.ANTI_ALIAS_FLAG).apply {
    textSize = 50f
    color = Color.BLACK
}

fun provideBitmap(value: Long): Bitmap {
    var w = 1080 / 2
    var h = w / 2

    if (value % 5 == 0L) {
        if (value % 10 == 0L) {
            h = 15 * w

            val b = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565)
            val c = Canvas(b)
            paint2.shader = LinearGradient(0f, 0f, w.toFloat(), h.toFloat(), intArrayOf(Color.BLUE, Color.WHITE, Color.YELLOW, Color.RED), null, Shader.TileMode.CLAMP)
            c.drawRect(0f, 0f, w.toFloat(), h.toFloat(), paint2)
            val xxx = w / 6f
            val yyy = h / 20f
            repeat(6) { x ->
                repeat(20) { h ->
                    c.drawText(value.toString(), xxx * x, yyy * h, paint)
                }
            }
            return b
        } else {
            w *= 10
            val b = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565)
            val c = Canvas(b)
            paint2.shader = LinearGradient(0f, 0f, w.toFloat(), h.toFloat(), intArrayOf(Color.BLUE, Color.WHITE, Color.YELLOW, Color.RED), null, Shader.TileMode.CLAMP)
            c.drawRect(0f, 0f, w.toFloat(), h.toFloat(), paint2)
            val xxx = w / 20f
            val yyy = h / 6f
            repeat(20) { x ->
                repeat(6) { h ->
                    c.drawText(value.toString(), xxx * x, yyy * h, paint)
                }
            }
            return b
        }
    } else {
        val b = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565)
        val c = Canvas(b)
        c.drawColor(colors[(abs(value) % colors.size).toInt()])
        c.drawText(value.toString(), w / 2f, h / 2f, paint)
        return b
    }
}


fun saveBitmapFile(bitmap: Bitmap, file: File) {
    var bos: BufferedOutputStream? = null
    try {
        bos = BufferedOutputStream(FileOutputStream(file))
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos)
    } catch (e: IOException) {
        e.printStackTrace()
    } finally {
        bos?.flush()
        bos?.close()
    }
}


fun testFetchAfter(key: Long, callback: (List<MyViewerData>) -> Unit) {
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

fun testFetchBefore(key: Long, callback: (List<MyViewerData>) -> Unit) {
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

private fun numPhoto(value: Long) = MyViewerData(id = value)

fun isMainThread() = Looper.myLooper() == Looper.getMainLooper()
fun runOnIOThread(block: () -> Unit): Disposable = Observable.fromCallable { block() }.subscribeOn(Schedulers.io()).subscribe()
fun runOnUIThread(block: () -> Unit) {
    if (isMainThread()) block() else mainHandler.post(block)
}



