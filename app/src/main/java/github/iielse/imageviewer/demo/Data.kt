package github.iielse.imageviewer.demo

import android.os.Handler
import android.os.Looper
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

fun fetch(pageNo: Int, callback: (List<Feed>) -> Unit) {
    mainHandler.postDelayed({
        callback(gson.fromJson(when (pageNo) {
            0 -> page1
            1 -> page2
            2 -> page3
            else -> "[]"
        }, object : TypeToken<List<Feed>>() {}.type))
    }, 200)
}
private val mainHandler = Handler(Looper.getMainLooper())