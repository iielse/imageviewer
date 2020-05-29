package com.github.iielse.imageviewer.demo.data

import android.os.Handler
import android.os.Looper
import kotlin.math.max
import kotlin.math.min

// 模拟请求
object Api {
    // 模拟网络请求或者本地db查询 上一页
    fun asyncQueryBefore(id: Long, callback: (List<MyData>) -> Unit) {
        val idx = myData.indexOfFirst { it.id == id }
        Handler(Looper.getMainLooper()).postDelayed({
            if (idx < 0) {
                callback(emptyList())
                return@postDelayed
            }
            val result = myData.subList(max(idx - PAGE_SIZE, 0), idx)
            callback(result)
        }, 100)
    }

    // 模拟网络请求或者本地db查询 下一页
    fun asyncQueryAfter(id: Long, callback: (List<MyData>) -> Unit) {
        val idx = myData.indexOfFirst { it.id == id }
        Handler(Looper.getMainLooper()).postDelayed({
            if (idx < 0) {
                callback(emptyList())
                return@postDelayed
            }

            val result = myData.subList(idx + 1, min(idx + PAGE_SIZE, myData.size - 1))
            callback(result)
        }, 100)
    }
}