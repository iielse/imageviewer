package com.github.iielse.imageviewer.demo

import android.content.Context
import android.graphics.*
import android.os.Looper
import android.widget.Toast
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

fun saveBitmapFile(bitmap: Bitmap, file: File) {
    var bos: BufferedOutputStream? = null
    try {
        bos = BufferedOutputStream(FileOutputStream(file))
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos)
        bos.flush()
    } catch (e: IOException) {
        e.printStackTrace()
    } finally {
        try {
            bos?.close()
        } catch (ignore: Exception) {
        }
    }
}


fun isMainThread() = Looper.myLooper() == Looper.getMainLooper()
fun runOnIOThread(block: () -> Unit): Disposable = Observable.fromCallable(block).subscribeOn(Schedulers.io()).subscribe()
fun runOnUIThread(block: () -> Unit) {
    if (isMainThread()) block() else mainHandler.post(block)
}

object X {
    var appContext: Context? = null
}

fun appContext() = X.appContext!!
fun toast(message: String) = runOnUIThread { Toast.makeText(appContext(), message, Toast.LENGTH_SHORT).show() }

fun statusBarHeight(): Int {
    var height = 0
    val resourceId = appContext().getResources().getIdentifier("status_bar_height", "dimen", "android");
    if (resourceId > 0) {
        height = appContext().getResources().getDimensionPixelSize(resourceId);
    }
    return height
}