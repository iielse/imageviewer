package com.github.iielse.imageviewer.core

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.View

//val View.activity: Activity?
//    get() = getActivity(context)
//
//private fun getActivity(context: Context?): Activity? {
//    if (context == null) return null
//    if (context is Activity) return context
//    if (context is ContextWrapper) return getActivity(context.baseContext)
//    return null
//}


fun <T> List<T>.safelySubList(fromIndex: Int, toIndex: Int): List<T> {
    if (fromIndex > toIndex) return emptyList()
    return subList(fromIndex, toIndex)
}