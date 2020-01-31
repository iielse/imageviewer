package com.github.iielse.imageviewer.utils

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.forEach
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent

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

fun LifecycleOwner.onDestroy(callback: () -> Unit) {
    lifecycle.addObserver(object : LifecycleObserver {
        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        fun onDestroy() = callback()
    })
}

fun Any.log(tag: String = "test", block: () -> String) {
    if (Config.DEBUG) Log.i(tag, block())
}

