package com.github.iielse.imageviewer.adapter

data class Item(
        val type: Int,
        val id: Int,
        val extra: Any? = null
) {
    inline fun <reified T> extra(): T? {
        return extra as? T?
    }
}