package com.github.iielse.imageviewer.core

data class PWrapper(
        val type: Int,
        val id: String,
        val extra: Any? = null
) {
    inline fun <reified T> get(): T? {
        return extra as? T?
    }
}