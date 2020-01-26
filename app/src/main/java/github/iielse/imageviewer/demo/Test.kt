package github.iielse.imageviewer.demo

import android.graphics.drawable.Drawable
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import kotlin.math.abs


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

fun provideBitmap(value: Int): Bitmap {
    val w = 1080 / 2
    val h = w / 2
    val b = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565)
    val c = Canvas(b)
    c.drawColor(colors[abs(value) % colors.size])
    c.drawText(value.toString(), w / 2f, h / 2f, paint)
    return b
}