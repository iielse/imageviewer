package com.github.iielse.imageviewer.util

import android.animation.TypeEvaluator
import android.graphics.Color
import android.graphics.Rect
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator

object ViewerUtil {
    private val accelerateInterpolator by lazy { AccelerateDecelerateInterpolator() }
    val colorEvaluator by lazy {
        TypeEvaluator<Int> { fraction, startValue, endValue ->
            val f = accelerateInterpolator.getInterpolation(fraction)
            val startColor = startValue!!
            val endColor = endValue!!
            val alpha = (Color.alpha(startColor) + f * (Color.alpha(endColor) - Color.alpha(startColor))).toInt()
            val red = (Color.red(startColor) + f * (Color.red(endColor) - Color.red(startColor))).toInt()
            val green = (Color.green(startColor) + f * (Color.green(endColor) - Color.green(startColor))).toInt()
            val blue = (Color.blue(startColor) + f * (Color.blue(endColor) - Color.blue(startColor))).toInt()
            Color.argb(alpha, red, green, blue)
        }
    }

    fun transform(originView: View, current: View) {

    }

}