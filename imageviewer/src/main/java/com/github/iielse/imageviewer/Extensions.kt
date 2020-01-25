package com.github.iielse.imageviewer

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.forEach

val Float.decimal2: Float get() = (this * 100).toInt() / 100f

fun ViewGroup.inflate(resId: Int): View {
    return LayoutInflater.from(context).inflate(resId, this, false)
}

fun ViewGroup.findViewWithKeyTag(key: Int, tag: Any): View? {
    forEach {
        if (it.getTag(key) == tag) return it
        if (it is ViewGroup) {
            val result = it.findViewWithKeyTag(key, tag)
            if (result != null) return result
        }
    }
    return null
}

fun log(tag: String = "viewer", block: () -> String) {
    if (Config.DEBUG) Log.i(tag, block())
}

