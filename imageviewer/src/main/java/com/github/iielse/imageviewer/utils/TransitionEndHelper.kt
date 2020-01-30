package com.github.iielse.imageviewer.utils

import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.*
import com.github.iielse.imageviewer.viewholders.PhotoViewHolder
import kotlinx.android.synthetic.main.item_imageviewer_photo.*

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
                holder.photoView.scaleX = 1f
                holder.photoView.scaleY = 1f
                holder.photoView.layoutParams = holder.photoView.layoutParams.apply {
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

    private fun transitionSet(): Transition {
        return TransitionSet().apply {
            addTransition(ChangeBounds())
            addTransition(ChangeImageTransform())
            addTransition(ChangeTransform())
            duration = Config.DURATION_TRANSITION
            interpolator = DecelerateInterpolator()
        }
    }
}