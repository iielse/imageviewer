package com.github.iielse.imageviewer.demo.core

fun <T> List<T>.add(item: T?): List<T> {
    if (item == null) return this
    return toMutableList().apply { add(item) }
}

fun <T> List<T>.add(index: Int, item: T?): List<T> {
    if (item == null) return this
    return toMutableList().apply { add(index, item) }
}

 fun <T> List<T>.addAll(items: List<T>?): List<T> {
    if (items == null) return this
    return toMutableList().apply { addAll(items) }
}

fun <T> List<T>.insertOrReplace(item: T?): List<T> {
    if (item == null) return this
    return toMutableList().apply {
        if (contains(item)) remove(item)
        add(item)
    }
}

fun <T> List<T>.insertOrReplace(index: Int, item: T?): List<T> {
    if (item == null) return this
    return toMutableList().apply {
        if (contains(item)) remove(item)
        add(index, item)
    }
}

fun <T, K> Map<T, K>.insertOrReplace(
    key: T,
    value: K
): Map<T, K> {
    return toMutableMap().apply { put(key, value) }
}

fun <T, K> Map<T, K>.insertOrReplace(items: List<Pair<T, K>>?): Map<T, K> {
    if (items == null) return this
    return toMutableMap().apply { putAll(items) }
}

fun <T> List<T>.replaceIf(block: (T) -> T?): List<T> {
    return toMutableList().apply {
        forEachIndexed { index, t ->
            block(t)?.let { set(index, it) }
        }
    }
}

fun <T, K> Map<T, K>.replaceIf(block: (K) -> K?): Map<T, K> {
    return toMutableMap().apply {
        forEach { item -> block(item.value)?.let { set(item.key, it) }    }
    }
}

fun <T> List<T>.remove(item: T?): List<T> {
    if (item == null) return this
    return toMutableList().apply { remove(item) }
}

fun <T> List<T>.removeAll(item: List<T>?): List<T> {
    if (item == null) return this
    return toMutableList().apply { removeAll(item) }
}

fun <T> List<T>.removeIf(
    condition: (T) -> Boolean
): List<T> {
    val list = toMutableList()
    val i = list.iterator()
    while (i.hasNext()) {
        if (condition(i.next())) i.remove()
    }
    return list
}


