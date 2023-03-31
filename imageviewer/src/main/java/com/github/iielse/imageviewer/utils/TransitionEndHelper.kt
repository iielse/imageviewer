package com.github.iielse.imageviewer.utils

import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import androidx.core.view.ViewCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.*
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.*
import com.github.iielse.imageviewer.R
import com.github.iielse.imageviewer.viewholders.PhotoViewHolder
import com.github.iielse.imageviewer.viewholders.SubsamplingViewHolder
import com.github.iielse.imageviewer.viewholders.VideoViewHolder
import kotlin.math.max

object TransitionEndHelper {
    val transitionAnimating get() = animating
    private var animating = false

    fun end(fragment: DialogFragment, startView: View?, holder: RecyclerView.ViewHolder) {
        beforeTransition(startView, holder)
        val doTransition = {
            TransitionManager.beginDelayedTransition(holder.itemView as ViewGroup, transitionSet().also {
                it.addListener(object : TransitionListenerAdapter() {
                    override fun onTransitionStart(transition: Transition) {
                        animating = true
                    }

                    override fun onTransitionEnd(transition: Transition) {
                        if (!animating) return
                        animating = false
                        fragment.dismissAllowingStateLoss()
                    }
                })
            })
            transition(startView, holder)
        }
        holder.itemView.post(doTransition)

        fragment.lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                if (event == Lifecycle.Event.ON_DESTROY) {
                    fragment.lifecycle.removeObserver(this)
                    animating = false
                    holder.itemView.removeCallbacks(doTransition)
                    TransitionManager.endTransitions(holder.itemView as ViewGroup)
                }
            }
        })
    }

    private fun beforeTransition(startView: View?, holder: RecyclerView.ViewHolder) {
        when (holder) {
            is VideoViewHolder -> {
                holder.binding.imageView.translationX = holder.binding.videoView.translationX
                holder.binding.imageView.translationY = holder.binding.videoView.translationY
                holder.binding.imageView.scaleX = holder.binding.videoView.scaleX
                holder.binding.imageView.scaleY = holder.binding.videoView.scaleY
                holder.binding.imageView.visibility = View.VISIBLE
                holder.binding.videoView.visibility = View.GONE
            }
        }
    }

    private fun transition(startView: View?, holder: RecyclerView.ViewHolder) {
        when (holder) {
            is PhotoViewHolder -> {
                holder.binding.photoView.scaleType = (startView as? ImageView?)?.scaleType
                        ?: ImageView.ScaleType.FIT_CENTER
                holder.binding.photoView.translationX = 0f
                holder.binding.photoView.translationY = 0f
                holder.binding.photoView.scaleX = if (startView != null) 1f else 2f
                holder.binding.photoView.scaleY = if (startView != null) 1f else 2f
                // holder.photoView.alpha = startView?.alpha ?: 0f
                fade(holder, startView)
                holder.binding.photoView.layoutParams = holder.binding.photoView.layoutParams.apply {
                    width = startView?.width ?: width
                    height = startView?.height ?: height
                    val location = IntArray(2)
                    getLocationOnScreen(startView, location)
                    if (this is ViewGroup.MarginLayoutParams) {
                        marginStart = location[0] - Config.TRANSITION_OFFSET_X
                        topMargin = location[1] - Config.TRANSITION_OFFSET_Y
                    }
                }
            }
            is SubsamplingViewHolder -> {
                holder.binding.subsamplingView.translationX = 0f
                holder.binding.subsamplingView.translationY = 0f
                holder.binding.subsamplingView.scaleX = 2f
                holder.binding.subsamplingView.scaleY = 2f
                // holder.photoView.alpha = startView?.alpha ?: 0f
                fade(holder) // https://github.com/davemorrissey/subsampling-scale-image-view/issues/313
                holder.binding.subsamplingView.layoutParams = holder.binding.subsamplingView.layoutParams.apply {
                    width = startView?.width ?: width
                    height = startView?.height ?: height
                    val location = IntArray(2)
                    getLocationOnScreen(startView, location)
                    if (this is ViewGroup.MarginLayoutParams) {
                        marginStart = location[0] - Config.TRANSITION_OFFSET_X
                        topMargin = location[1] - Config.TRANSITION_OFFSET_Y
                    }
                }
            }
            is VideoViewHolder -> {
                holder.binding.imageView.translationX = 0f
                holder.binding.imageView.translationY = 0f
                holder.binding.imageView.scaleX = if (startView != null) 1f else 2f
                holder.binding.imageView.scaleY = if (startView != null) 1f else 2f
                // holder.photoView.alpha = startView?.alpha ?: 0f
                fade(holder, startView)
                holder.binding.videoView.pause()
                holder.binding.imageView.layoutParams = holder.binding.imageView.layoutParams.apply {
                    width = startView?.width ?: width
                    height = startView?.height ?: height
                    val location = IntArray(2)
                    getLocationOnScreen(startView, location)
                    if (this is ViewGroup.MarginLayoutParams) {
                        marginStart = location[0] - Config.TRANSITION_OFFSET_X
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

    private fun fade(holder: RecyclerView.ViewHolder, startView: View? = null) {
        when (holder) {
            is PhotoViewHolder -> {
                if (startView != null) {
                    holder.binding.photoView.animate()
                            .setDuration(0)
                            .setStartDelay(max(Config.DURATION_TRANSITION - 20, 0))
                            .alpha(0f).start()
                } else {
                    holder.binding.photoView.animate().setDuration(Config.DURATION_TRANSITION)
                            .alpha(0f).start()
                }
            }
            is SubsamplingViewHolder -> {
                holder.binding.subsamplingView.animate().setDuration(Config.DURATION_TRANSITION)
                        .alpha(0f).start()
            }
            is VideoViewHolder -> {
                if (startView != null) {
                    holder.binding.imageView.animate()
                            .setDuration(0)
                            .setStartDelay(max(Config.DURATION_TRANSITION - 20, 0))
                            .alpha(0f).start()
                } else {
                    holder.binding.imageView.animate().setDuration(Config.DURATION_TRANSITION)
                            .alpha(0f).start()
                }
            }
        }
    }

    private fun getLocationOnScreen(startView: View?, location: IntArray) {
        startView?.getLocationOnScreen(location)

        if (location[0] == 0) {
            location[0] = (startView?.getTag(R.id.viewer_start_view_location_0) as? Int) ?: 0
        }
        if (location[1] == 0) {
            location[1] = (startView?.getTag(R.id.viewer_start_view_location_1) as? Int) ?: 0
        }

        if (startView?.layoutDirection == ViewCompat.LAYOUT_DIRECTION_RTL) {
            location[0] = startView.context.resources.displayMetrics.widthPixels - location[0] - startView.width
        }
    }
}