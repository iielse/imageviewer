package com.github.iielse.imageviewer.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.LifecycleOwner

object AnimHelper {
    fun start(owner: LifecycleOwner, startView: View?, endView: View) {
        endView.doOnPreDraw {
            val animator = ValueAnimator.ofFloat(0f, 1f)
            animator.duration = Config.DURATION_TRANSFORMER
            animator.interpolator = DecelerateInterpolator()
            if (startView == null) {
                animator.addUpdateListener {
                    val fraction = it.animatedValue as Float
                    endView.alpha = fraction
                }
            } else {
                val w1 = startView.width
                val w2 = endView.width
                val h1 = startView.height
                val h2 = endView.height
                animator.addUpdateListener {
                    val fraction = it.animatedValue as Float
                    endView.layoutParams = (endView.layoutParams as? ViewGroup.MarginLayoutParams)?.apply {
                        width = (w1 + (w2 - w1) * fraction).toInt()
                        height = (h1 + (h2 - h1) * fraction).toInt()
                        setMargins((startView.left * (1 - fraction)).toInt(), (startView.top * (1 - fraction)).toInt(), 0, 0)
                    }
                }
            }
            animator.start()
            owner.onDestroy { animator.cancel() }
        }
    }

    fun end(fragment: DialogFragment, startView: View?, endView: View) {
        val animator = ValueAnimator.ofFloat(1f, 0f)
        animator.duration = 300
        animator.interpolator = DecelerateInterpolator()
        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                fragment.dismissAllowingStateLoss()
            }
        })

        if (startView == null) {
            animator.addUpdateListener {
                val fraction = it.animatedValue as Float
                endView.alpha = fraction
                endView.scaleX = 1 + (1 - fraction) * 0.4f
                endView.scaleY = 1 + (1 - fraction) * 0.4f
            }
        } else {
            val w1 = startView.width
            val w2 = endView.width
            val h1 = startView.height
            val h2 = endView.height

            val transX = endView.translationX
            val transY = endView.translationY
            val scaleX = endView.scaleX
            val scaleY = endView.scaleY
            animator.addUpdateListener {
                val fraction = it.animatedValue as Float
                endView.translationX = fraction * transX
                endView.translationY = fraction * transY
                endView.scaleX = 1 + (scaleX - 1) * fraction
                endView.scaleY = 1 + (scaleY - 1) * fraction
                endView.layoutParams = (endView.layoutParams as? ViewGroup.MarginLayoutParams)?.apply {
                    width = (w1 + (w2 - w1) * fraction).toInt()
                    height = (h1 + (h2 - h1) * fraction).toInt()
                    setMargins((startView.left * (1 - fraction)).toInt(), (startView.top * (1 - fraction)).toInt(), 0, 0)
                }
            }

        }

        animator.start()
        fragment.onDestroy { animator.cancel() }
    }
}