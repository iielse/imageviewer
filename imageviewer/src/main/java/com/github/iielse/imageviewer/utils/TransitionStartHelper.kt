package com.github.iielse.imageviewer.utils

import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.*
import com.github.iielse.imageviewer.viewholders.PhotoViewHolder
import com.github.iielse.imageviewer.viewholders.SubsamplingViewHolder
import kotlinx.android.synthetic.main.item_imageviewer_photo.*
import kotlinx.android.synthetic.main.item_imageviewer_subsampling.*

object TransitionStartHelper {
    var animating = false

    fun start(owner: LifecycleOwner, startView: View?, holder: RecyclerView.ViewHolder) {
        beforeTransition(startView, holder)
        val doTransition = {
            TransitionManager.beginDelayedTransition(holder.itemView as ViewGroup, transitionSet().also {
                it.addListener(object : TransitionListenerAdapter() {
                    override fun onTransitionStart(transition: Transition) {
                        animating = true
                    }

                    override fun onTransitionEnd(transition: Transition) {
                        animating = false
                    }
                })
            })
            transition(holder)
        }
        holder.itemView.postDelayed(doTransition, 50)
        owner.onDestroy {
            holder.itemView.removeCallbacks(doTransition)
            TransitionManager.endTransitions(holder.itemView as ViewGroup)
        }
    }

    private fun beforeTransition(startView: View?, holder: RecyclerView.ViewHolder) {
        when (holder) {
            is PhotoViewHolder -> {
                holder.photoView.scaleType = (startView as? ImageView?)?.scaleType
                        ?: ImageView.ScaleType.FIT_CENTER
                holder.photoView.layoutParams = holder.photoView.layoutParams.apply {
                    width = startView?.width ?: width
                    height = startView?.height ?: height
                    if (this is ViewGroup.MarginLayoutParams) {
                        marginStart = startView?.left ?: marginStart
                        topMargin = startView?.top ?: topMargin
                    }
                }
            }
            is SubsamplingViewHolder -> {
                holder.subsamplingView.layoutParams = holder.subsamplingView.layoutParams.apply {
                    width = startView?.width ?: width
                    height = startView?.height ?: height
                    if (this is ViewGroup.MarginLayoutParams) {
                        marginStart = startView?.left ?: marginStart
                        topMargin = startView?.top ?: topMargin
                    }
                }
            }
        }
    }

    private fun transition(holder: RecyclerView.ViewHolder) {
        when (holder) {
            is PhotoViewHolder -> {
                holder.photoView.scaleType = ImageView.ScaleType.FIT_CENTER
                holder.photoView.layoutParams = holder.photoView.layoutParams.apply {
                    width = MATCH_PARENT
                    height = MATCH_PARENT
                    if (this is ViewGroup.MarginLayoutParams) {
                        marginStart = 0
                        topMargin = 0
                    }
                }
            }
            is SubsamplingViewHolder -> {
                holder.subsamplingView.layoutParams = holder.subsamplingView.layoutParams.apply {
                    width = MATCH_PARENT
                    height = MATCH_PARENT
                    if (this is ViewGroup.MarginLayoutParams) {
                        marginStart = 0
                        topMargin = 0
                    }
                }
            }
        }
    }

    private fun transitionSet(): Transition {
        return TransitionSet().apply {
            addTransition(ChangeBounds())
            addTransition(ChangeImageTransform())
            // https://github.com/davemorrissey/subsampling-scale-image-view/issues/313
            duration = Config.DURATION_TRANSITION
            interpolator = DecelerateInterpolator()
        }
    }
}