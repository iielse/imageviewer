package com.github.iielse.imageviewer

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

val Float.decimal2: Float get() = (this * 100).toInt() / 100f

fun ViewGroup.inflate(resId: Int): View {
    return LayoutInflater.from(context).inflate(resId, this, false)
}

fun log(tag: String ="viewer", block:()->String) {
    if (Config.DEBUG) Log.i(tag, block())
}