//package com.github.iielse.imageviewer.utils
//
//import android.animation.Animator
//import android.animation.AnimatorListenerAdapter
//import android.animation.ValueAnimator
//import android.transition.AutoTransition
//import android.transition.Transition
//import android.transition.TransitionManager
//import android.view.View
//import android.view.ViewGroup
//import android.view.animation.DecelerateInterpolator
//import android.widget.ImageView
//import androidx.core.view.doOnPreDraw
//import androidx.lifecycle.LifecycleOwner
//import com.github.iielse.imageviewer.widgets.PhotoView2
//
//
//object AnimStartHelper {
//    private var animating = false
//
//    fun start(owner: LifecycleOwner, startView: View?, endView: View) {
//        if (animating) return
//
//        endView.doOnPreDraw {
//            val animator = ValueAnimator.ofFloat(0f, 1f)
//            animator.duration = Config.DURATION_TRANSITION
//            animator.startDelay = Config.DURATION_ENTER_START_DELAY
//            animator.interpolator = DecelerateInterpolator()
//
//            animator.addListener(object : AnimatorListenerAdapter() {
//                override fun onAnimationEnd(animation: Animator?) {
//                    (endView as? PhotoView2?)?.scaleType = ImageView.ScaleType.FIT_CENTER
//                    animating = false
//                }
//
//                override fun onAnimationCancel(animation: Animator?) {
//                    animating = false
//                }
//
//                override fun onAnimationStart(animation: Animator?) {
//                    animating = true
//                }
//            })
//
//            if (startView == null) {
//                animator.addUpdateListener {
//                    val fraction = it.animatedValue as Float
//                    endView.alpha = fraction
//                }
//            } else {
//                val w1 = startView.width
//                val w2 = endView.width
//                val h1 = startView.height
//                val h2 = endView.height
//
//                val animTransform: ((Float) -> Unit) = { fraction ->
//                    endView.layoutParams = (endView.layoutParams as? ViewGroup.MarginLayoutParams)?.apply {
//                        width = (w1 + (w2 - w1) * fraction).toInt()
//                        height = (h1 + (h2 - h1) * fraction).toInt()
//                        setMargins((startView.left * (1 - fraction)).toInt(), (startView.top * (1 - fraction)).toInt(), 0, 0)
//                    }
//                }
//
//                animTransform(0f)
//                animator.addUpdateListener { animTransform(it.animatedValue as Float) }
//            }
//            animator.start()
//            owner.onDestroy { animator.cancel() }
//        }
//    }
//}