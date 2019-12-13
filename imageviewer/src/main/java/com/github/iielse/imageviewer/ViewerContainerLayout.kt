package com.github.iielse.imageviewer

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout

class ViewerContainerLayout @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs) {
    private val argbEvaluator by lazy { ArgbEvaluator() }
    private var bgColor = Color.TRANSPARENT
    private var animator: ObjectAnimator? = null

    fun animateBackgroundColor(targetColor: Int) {
        animator = ObjectAnimator.ofObject(this, "backgroundColor", argbEvaluator, bgColor, targetColor)
                .also { it.start() }
    }

    fun updateBackgroundColor(fraction: Float) {
        val resultColor: Int = argbEvaluator.evaluate(fraction, 0xff000000.toInt(), 0x00000000) as Int
        setBackgroundColor(resultColor)
    }

    fun release() {
        animator?.cancel()
    }

    override fun setBackgroundColor(color: Int) {
        super.setBackgroundColor(color)
        bgColor = color
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        release()
    }
}