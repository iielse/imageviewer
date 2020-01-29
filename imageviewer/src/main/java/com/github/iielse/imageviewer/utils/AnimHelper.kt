package com.github.iielse.imageviewer.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.LifecycleOwner
import com.github.iielse.imageviewer.widgets.PhotoView2

object AnimHelper {
    private var ending = false // end animation running

    fun start(owner: LifecycleOwner, startView: View?, endView: View) {
        endView.doOnPreDraw {
            val animator = ValueAnimator.ofFloat(0f, 1f)
            animator.duration = Config.DURATION_TRANSFORMER
            animator.startDelay = Config.DURATION_ENTER_START_DELAY
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

                val animTransform: ((Float) -> Unit) = { fraction ->
                    endView.layoutParams = (endView.layoutParams as? ViewGroup.MarginLayoutParams)?.apply {
                        width = (w1 + (w2 - w1) * fraction).toInt()
                        height = (h1 + (h2 - h1) * fraction).toInt()
                        setMargins((startView.left * (1 - fraction)).toInt(), (startView.top * (1 - fraction)).toInt(), 0, 0)
                    }
                }

                animTransform(0f)
                animator.addUpdateListener { animTransform(it.animatedValue as Float) }
            }
            animator.start()
            owner.onDestroy { animator.cancel() }
        }
    }

    fun end(fragment: DialogFragment, startView: View?, endView: View) {
        if (ending) return

        val animator = ValueAnimator.ofFloat(1f, 0f)
        animator.duration = Config.DURATION_TRANSFORMER
        animator.interpolator = DecelerateInterpolator()
        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                fragment.dismissAllowingStateLoss()
                ending = false
            }

            override fun onAnimationCancel(animation: Animator?) {
                ending = false
            }

            override fun onAnimationStart(animation: Animator?) {
                ending = true
            }
        })

        if (startView == null) {
            animator.addUpdateListener {
                val fraction = it.animatedValue as Float
                endView.alpha = fraction
                endView.scaleX = 1 + (1 - fraction) * 0.4f
                endView.scaleY = 1 + (1 - fraction) * 0.4f
            }
        } else if (startView is ImageView && startView.scaleType == ImageView.ScaleType.CENTER_CROP &&
                endView is PhotoView2 && endView.drawable != null) {
            val startWidth = startView.width
            val startHeight = startView.height
            val endWidth = endView.width
            val endHeight = endView.height
            val endDWidth = endView.drawable.minimumWidth
            val endDHeight = endView.drawable.minimumHeight
            val offsetWidth = endWidth - endDWidth
            val offsetHeight = endHeight - endDHeight
            val transX = endView.translationX + offsetWidth / 2
            val transY = endView.translationY + offsetHeight / 2
            endView.scaleType = ImageView.ScaleType.CENTER_CROP
            val scaleX = endView.scaleX
            val scaleY = endView.scaleY
            animator.addUpdateListener {
                val fraction = it.animatedValue as Float
                endView.translationX = fraction * transX
                endView.translationY = fraction * transY
                endView.scaleX = 1 + (scaleX - 1) * fraction
                endView.scaleY = 1 + (scaleY - 1) * fraction
                endView.layoutParams = (endView.layoutParams as? ViewGroup.MarginLayoutParams)?.apply {
                    width = (startWidth + (endDWidth - startWidth) * fraction).toInt()
                    height = (startHeight + (endDHeight - startHeight) * fraction).toInt()
                    setMargins((startView.left * (1 - fraction)).toInt(), (startView.top * (1 - fraction)).toInt(), 0, 0)
                }
            }
        } else {
            val startWidth = startView.width
            val endWidth = endView.width
            val startHeight = startView.height
            val endHeight = endView.height
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
                    width = (startWidth + (endWidth - startWidth) * fraction).toInt()
                    height = (startHeight + (endHeight - startHeight) * fraction).toInt()
                    setMargins((startView.left * (1 - fraction)).toInt(), (startView.top * (1 - fraction)).toInt(), 0, 0)
                }
            }
        }
        animator.start()
        fragment.onDestroy { animator.cancel() }
    }
}