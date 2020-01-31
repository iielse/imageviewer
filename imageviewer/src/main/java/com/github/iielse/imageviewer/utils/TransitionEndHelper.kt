package com.github.iielse.imageviewer.utils

import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.*
import com.github.iielse.imageviewer.viewholders.PhotoViewHolder
import com.github.iielse.imageviewer.viewholders.SubsamplingViewHolder
import kotlinx.android.synthetic.main.item_imageviewer_photo.*
import kotlinx.android.synthetic.main.item_imageviewer_subsampling.*

object TransitionEndHelper {
    var animating = false

    fun end(fragment: DialogFragment, startView: View?, holder: RecyclerView.ViewHolder) {
        val doTransition = {
            TransitionManager.beginDelayedTransition(holder.itemView as ViewGroup, transitionSet().also {
                it.addListener(object : TransitionListenerAdapter() {
                    override fun onTransitionStart(transition: Transition) {
                        animating = true
                    }

                    override fun onTransitionEnd(transition: Transition) {
                        animating = false
                        holder.itemView.post { fragment.dismissAllowingStateLoss() }
                    }
                })
            })
            transition(startView, holder)
        }
        holder.itemView.post(doTransition)
        fragment.onDestroy {
            holder.itemView.removeCallbacks(doTransition)
            TransitionManager.endTransitions(holder.itemView as ViewGroup)
        }
    }

    private fun transition(startView: View?, holder: RecyclerView.ViewHolder) {
        when (holder) {
            is PhotoViewHolder -> {
                holder.photoView.scaleType = (startView as? ImageView?)?.scaleType
                        ?: ImageView.ScaleType.FIT_CENTER
                holder.photoView.translationX = 0f
                holder.photoView.translationY = 0f
                holder.photoView.scaleX = if (startView != null) 1f else 2f
                holder.photoView.scaleY = if (startView != null) 1f else 2f
                // holder.photoView.alpha = startView?.alpha ?: 0f
                if (startView == null) fade(holder)
                holder.photoView.layoutParams = holder.photoView.layoutParams.apply {
                    width = startView?.width ?: width
                    height = startView?.height ?: height
                    val location = IntArray(2)
                    startView?.getLocationOnScreen(location)
                    if (this is ViewGroup.MarginLayoutParams) {
                        marginStart = location[0]
                        topMargin = location[1] - Config.TRANSITION_OFFSET_Y
                    }
                }
            }
            is SubsamplingViewHolder -> {
                holder.subsamplingView.translationX = 0f
                holder.subsamplingView.translationY = 0f
                holder.subsamplingView.scaleX = 2f
                holder.subsamplingView.scaleY = 2f
                // holder.photoView.alpha = startView?.alpha ?: 0f
                fade(holder) // https://github.com/davemorrissey/subsampling-scale-image-view/issues/313
                holder.subsamplingView.layoutParams = holder.subsamplingView.layoutParams.apply {
                    width = startView?.width ?: width
                    height = startView?.height ?: height
                    val location = IntArray(2)
                    startView?.getLocationOnScreen(location)
                    if (this is ViewGroup.MarginLayoutParams) {
                        marginStart = location[0]
                        topMargin = location[1] - Config.TRANSITION_OFFSET_Y
                    }
                }
            }
        }
    }

    private fun transitionSet(): Transition {
        return TransitionSet().apply {
            addTransition(ChangeBounds())
            addTransition(ChangeImageTransform())
            addTransition(ChangeTransform())
            // addTransition(Fade())
            duration = Config.DURATION_TRANSITION
            interpolator = DecelerateInterpolator()
        }
    }

    private fun fade(holder: RecyclerView.ViewHolder) {
        when (holder) {
            is PhotoViewHolder -> {
                holder.photoView.animate().setDuration(Config.DURATION_TRANSITION)
                        .alpha(0f).start()
            }
            is SubsamplingViewHolder -> {
                holder.subsamplingView.animate().setDuration(Config.DURATION_TRANSITION)
                        .alpha(0f).start()
            }
        }
    }
}