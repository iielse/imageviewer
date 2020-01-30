//package com.github.iielse.imageviewer.utils
//
//import android.animation.Animator
//import android.animation.AnimatorListenerAdapter
//import android.animation.ValueAnimator
//import android.view.View
//import android.view.ViewGroup
//import android.view.animation.DecelerateInterpolator
//import android.widget.ImageView
//import androidx.fragment.app.DialogFragment
//import com.github.iielse.imageviewer.widgets.PhotoView2
//
//object AnimEndHelper {
//    private var animating = false
//
//    fun end(fragment: DialogFragment, startView: View?, endView: View) {
//        if (animating) return
//
//        val animator = ValueAnimator.ofFloat(1f, 0f)
//        animator.duration = Config.DURATION_TRANSFORMER
//        animator.interpolator = DecelerateInterpolator()
//        animator.addListener(object : AnimatorListenerAdapter() {
//            override fun onAnimationEnd(animation: Animator?) {
//                fragment.dismissAllowingStateLoss()
//                animating = false
//            }
//
//            override fun onAnimationCancel(animation: Animator?) {
//                animating = false
//            }
//
//            override fun onAnimationStart(animation: Animator?) {
//                animating = true
//            }
//        })
//
//        if (startView == null) {
//            animator.addUpdateListener {
//                val fraction = it.animatedValue as Float
//                endView.alpha = fraction
//                endView.scaleX = 1 + (1 - fraction) * 0.4f
//                endView.scaleY = 1 + (1 - fraction) * 0.4f
//            }
//        } else if (startView is ImageView && startView.scaleType == ImageView.ScaleType.CENTER_CROP &&
//                endView is PhotoView2 && endView.drawable != null) {
//            val startWidth = startView.width
//            val startHeight = startView.height
//            val endWidth = endView.width
//            val endHeight = endView.height
//            val endDWidth = endView.drawable.minimumWidth
//            val endDHeight = endView.drawable.minimumHeight
//            val offsetWidth = endWidth - endDWidth
//            val offsetHeight = endHeight - endDHeight
//            val transX = endView.translationX + offsetWidth / 2
//            val transY = endView.translationY + offsetHeight / 2
//            endView.scaleType = ImageView.ScaleType.CENTER_CROP
//            val scaleX = endView.scaleX
//            val scaleY = endView.scaleY
//            animator.addUpdateListener {
//                val fraction = it.animatedValue as Float
//                endView.translationX = fraction * transX
//                endView.translationY = fraction * transY
//                endView.scaleX = 1 + (scaleX - 1) * fraction
//                endView.scaleY = 1 + (scaleY - 1) * fraction
//                endView.layoutParams = (endView.layoutParams as? ViewGroup.MarginLayoutParams)?.apply {
//                    width = (startWidth + (endDWidth - startWidth) * fraction).toInt()
//                    height = (startHeight + (endDHeight - startHeight) * fraction).toInt()
//                    setMargins((startView.left * (1 - fraction)).toInt(), (startView.top * (1 - fraction)).toInt(), 0, 0)
//                }
//            }
//        } else {
//            val startWidth = startView.width
//            val endWidth = endView.width
//            val startHeight = startView.height
//            val endHeight = endView.height
//            val transX = endView.translationX
//            val transY = endView.translationY
//            val scaleX = endView.scaleX
//            val scaleY = endView.scaleY
//            animator.addUpdateListener {
//                val fraction = it.animatedValue as Float
//                endView.translationX = fraction * transX
//                endView.translationY = fraction * transY
//                endView.scaleX = 1 + (scaleX - 1) * fraction
//                endView.scaleY = 1 + (scaleY - 1) * fraction
//                endView.layoutParams = (endView.layoutParams as? ViewGroup.MarginLayoutParams)?.apply {
//                    width = (startWidth + (endWidth - startWidth) * fraction).toInt()
//                    height = (startHeight + (endHeight - startHeight) * fraction).toInt()
//                    setMargins((startView.left * (1 - fraction)).toInt(), (startView.top * (1 - fraction)).toInt(), 0, 0)
//                }
//            }
//        }
//        animator.start()
//        fragment.onDestroy { animator.cancel() }
//    }
//}