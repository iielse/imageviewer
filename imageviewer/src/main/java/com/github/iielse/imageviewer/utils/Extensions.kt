package com.github.iielse.imageviewer.utils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.forEach
import androidx.lifecycle.*

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

fun <T : ViewModel> View.provideViewModel(modelClass: Class<T>): T? {
    return (activity as? ViewModelStoreOwner?)?.let { ViewModelProvider(it).get(modelClass) }
}

val View.activity: Activity?
    get() = getActivity(context)

// https://stackoverflow.com/questions/9273218/is-it-always-safe-to-cast-context-to-activity-within-view/45364110
private fun getActivity(context: Context?): Activity? {
    if (context == null) return null
    if (context is Activity) return context
    if (context is ContextWrapper) return getActivity(context.baseContext)
    return null
}

fun LifecycleOwner.onDestroy(callback: () -> Unit) {
    lifecycle.addObserver(object : LifecycleObserver {
        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        fun onDestroy() = callback()
    })
}

fun LifecycleOwner.onResume(callback: () -> Unit) {
    lifecycle.addObserver(object : LifecycleObserver {
        @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
        fun onResume() = callback()
    })
}

fun LifecycleOwner.onPause(callback: () -> Unit) {
    lifecycle.addObserver(object : LifecycleObserver {
        @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        fun onPause() = callback()
    })
}


