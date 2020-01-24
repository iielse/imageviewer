package com.github.iielse.imageviewer

import android.animation.TypeEvaluator
import android.animation.ValueAnimator
import android.graphics.Color
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.view.doOnPreDraw
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent

object ViewerUtil {




}

fun log(tag: String ="viewer", block:()->String) {
    if (BuildConfig.DEBUG) Log.i(tag, block())
}
