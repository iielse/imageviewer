package com.github.iielse.imageviewer.demo.data

import android.os.Handler
import android.os.Looper
import kotlin.math.max
import kotlin.math.min

// 模拟请求
class Api(
    private val data: List<MyData>
) {
    // 模拟网络请求或者本地db查询 上一页
    fun asyncQueryBefore(id: Long, callback: (List<MyData>) -> Unit) {
        val result = data.map { it.copy() }
        val idx = result.indexOfFirst { it.id == id }
        Handler(Looper.getMainLooper()).postDelayed({
            if (idx < 0) callback(emptyList())
            else callback(result.subList(max(idx - PAGE_SIZE, 0), idx))
        }, 100)
    }

    // 模拟网络请求或者本地db查询 下一页
    fun asyncQueryAfter(id: Long, callback: (List<MyData>) -> Unit) {
        val result = data.map { it.copy() }
        val idx = result.indexOfFirst { it.id == id }
        Handler(Looper.getMainLooper()).postDelayed({
            if (idx < 0) callback(emptyList())
            else callback(result.subList(idx + 1, max(idx + 1, min(idx + PAGE_SIZE, data.size - 1))))
        }, 100)
    }
}