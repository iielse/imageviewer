package com.github.iielse.imageviewer.demo.core

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.paging.PagedList
import androidx.paging.toLiveData
import com.github.iielse.imageviewer.demo.data.PAGE_SIZE

data class ListState<T>(
    val page: Int = PAGE_INITIAL,
    val nextKey: String? = null,
    val list: List<String> = listOf(),
    val data: Map<String, T> = mapOf()
)

inline fun <reified T> ListState<T>.reduceOnError(
    initial: Boolean
): ListState<T> {
    return if (initial) {
        this.copy(page = PAGE_INITIAL, list = listOf(ID_EMPTY))
    } else {
        this.copy(list = this.list.queryOver())
    }
}

inline fun <reified T> ListState<T>.reduceOnNext(
    initial: Boolean,
    data: List<T>,
    crossinline id: (T) -> String
): ListState<T> {
    val result: ListState<T>
    if (initial) {
        val count = data.size
        result = if (count == 0) {
            this.copy(page = PAGE_INITIAL, list = listOf(ID_EMPTY), data = mapOf())
        } else {
            this.copy(
                page = PAGE_INITIAL,
                nextKey = data.lastOrNull()?.let(id),
                list = listOf<String>().appendData(data) { id(it) },
                data = mapOf<String, T>().appendData(data) { id(it) }
            )
        }
    } else {
        val count = data.size
        result = if (count == 0) {
            this.copy(page = this.page, list = this.list.queryOver(), data = this.data)
        } else {
            this.copy(
                page = this.page + 1,
                nextKey = data.lastOrNull()?.let(id),
                list = this.list.appendData(data) { id(it) },
                data = this.data.appendData(data) { id(it) }
            )
        }
    }
    return result
}

fun <T> List<String>.appendData(
    data: List<T>,
    fetchMore: ()->Boolean = { data.size < PAGE_SIZE },
    id: (T) -> String
): List<String> {
    val dataList = data.map(id)
    return remove(ID_MORE_LOADING).addAll(dataList).let {
        if (fetchMore()) it.add(ID_NO_MORE) else it.add(ID_MORE_LOADING)
    }
}

inline fun <reified T, K> Map<T, K>.appendData(
    data: List<K>?,
    id: (K) -> T
): Map<T, K> {
    if (data == null) return this
    return toMutableMap().apply { putAll(data.map { Pair(id(it), it) }) }
}


fun List<String>.queryOver(): List<String> {
    return this.remove(ID_MORE_LOADING).add(ID_NO_MORE)
}

fun List<String>.mapToCell(start: Int, count: Int, childCell: (String) -> Cell): List<Cell> {
    return this.safelySubList(kotlin.math.max(0, start), kotlin.math.min(this.size, start + count))
        .map {
            superProcessCell(it) ?: childCell(it)
        }
}

fun <T> List<T>.safelySubList(
    fromIndex: Int,
    toIndex: Int
): List<T> {
    if (fromIndex > toIndex) return emptyList()
    if (fromIndex > size - 1) return emptyList()
    if (toIndex > size) return this.subList(fromIndex, size)
    return this.subList(fromIndex, toIndex)
}

fun <Key, Value> DataSource.Factory<Key, Value>.toLiveData(
    cellId: (Value) -> String,
    requestMore: () -> Unit
): LiveData<PagedList<Value>> {
    return this.toLiveData(pageSize = PAGE_SIZE,
        boundaryCallback = object : PagedList.BoundaryCallback<Value>() {
            override fun onItemAtEndLoaded(itemAtEnd: Value) {
                if (ID_MORE_LOADING == cellId(itemAtEnd)) {
                    requestMore()
                }
            }
        })
}

//////////////////////////////////////////////////////////////////