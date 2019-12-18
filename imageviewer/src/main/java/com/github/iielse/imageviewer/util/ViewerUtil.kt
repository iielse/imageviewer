package com.github.iielse.imageviewer.util

import android.animation.TypeEvaluator
import android.animation.ValueAnimator
import android.graphics.Color
import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.view.doOnPreDraw
import androidx.lifecycle.LifecycleOwner

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

    fun transform(owner: LifecycleOwner, startView: View?, endView: View) {
        if (startView == null) {
            endView.alpha = 0f
            endView.animate().alpha(1f).start()
        } else {
            endView.doOnPreDraw {
                val w1 = startView.width
                val w2 = endView.width
                val h1 = startView.height
                val h2 = endView.height
                ValueAnimator.ofFloat(0f, 1f).apply {
                    duration = 1000
                    addUpdateListener {
                        val fraction = it.animatedValue as Float
                        endView.layoutParams = (endView.layoutParams as? ViewGroup.MarginLayoutParams)?.apply {
                            width = (w1 + (w2 - w1) * fraction).toInt()
                            height = (h1 + (h2 - h1) * fraction).toInt()
                            setMargins((startView.left * (1 - fraction)).toInt(), (startView.top * (1 - fraction)).toInt(), 0, 0)
                        }
                    }
                }.start()
            }
        }
    }

}