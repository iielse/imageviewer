package com.github.iielse.imageviewer.adapter

data class Item(
        val type: Int,
        val id: Long,
        val extra: Any? = null
) {
    inline fun <reified T> extra(): T? {
        return extra as? T?
    }
}