package com.github.iielse.imageviewer.demo.data

import android.os.Handler
import android.os.Looper
import java.lang.IllegalStateException
import kotlin.math.max
import kotlin.math.min

// 模拟请求
class Api(
    private val data: List<MyData>
) {
    // 模拟网络请求或者本地db查询 上一页
    fun asyncQueryBefore(id: Long, pageSize: Int, callback: (List<MyData>) -> Unit) {
        val result = data.map { it.copy() }
        val idx = result.indexOfFirst { it.id == id }
        Handler(Looper.getMainLooper()).postDelayed({
            if (idx < 0) callback(emptyList())
            else callback(result.subList(max(idx - pageSize, 0), idx))
        }, 100)
    }

    // 模拟网络请求或者本地db查询 下一页
    fun asyncQueryAfter(id: Long?, pageSize: Int,  callback: (List<MyData>) -> Unit) {
        val result = data.map { it.copy() }
        val idx = result.indexOfFirst { it.id == id }
        Handler(Looper.getMainLooper()).postDelayed({
            when {
                id == -1L -> callback(result.subList(0, min(pageSize, result.size))) // 第一页
                id == null -> callback(emptyList()) // 查完了
                idx < 0 -> callback(emptyList())
                else -> callback(result.subList(idx + 1, max(idx + 1, min(idx + 1 + pageSize, result.size - 1))))
            }
        }, 100)
    }
}