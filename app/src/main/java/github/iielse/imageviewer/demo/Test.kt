package github.iielse.imageviewer.demo

import android.graphics.drawable.Drawable
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint


private val colors = listOf(
        0xfff2fefe,
        0xffece9f6,
        0xffc5c1d5,
        0xffe8dcea,
        0xfffbe8eb,
        0xfffff6f8
)
private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
    textSize = 20f
    color = Color.BLACK
}

fun provideBitmap(value: Int): Bitmap {
    val w = 1080 / 2
    val h = 1920 / 2
    val b = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565)
    val c = Canvas(b)
    c.drawColor(colors[value % colors.size])
    c.drawText(value.toString(), w / 2f, h / 2f, paint)
    return b
}