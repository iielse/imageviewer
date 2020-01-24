package com.github.iielse.imageviewer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

//val View.activity: Activity?
//    get() = getActivity(context)
//
//private fun getActivity(context: Context?): Activity? {
//    if (context == null) return null
//    if (context is Activity) return context
//    if (context is ContextWrapper) return getActivity(context.baseContext)
//    return null
//}

val Float.decimal2: Float get() = (this * 100).toInt() / 100f

fun ViewGroup.inflate(resId: Int): View {
    return LayoutInflater.from(context).inflate(resId, this, false)
}

fun <T> List<T>.safelySubList(fromIndex: Int, toIndex: Int): List<T> {
    if (fromIndex > toIndex) return emptyList()
    return subList(fromIndex, toIndex)
}


fun <T, K> Map<T, K>.mutablePutAll(items: List<Pair<T, K>>?): Map<T, K> {
    if (items == null) return this
    return toMutableMap().apply { putAll(items) }
}