package com.github.iielse.imageviewer

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import androidx.core.animation.addListener
import androidx.core.view.doOnPreDraw
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_image_viewer_dialog.*

class ImageViewerDialogFragment : BaseDialogFragment() {
    private val viewModel by lazy { ViewModelProviders.of(requireActivity()).get(ImageViewerViewModel::class.java) }
    private val adapter by lazy { ImageViewerAdapter() }
    private var animator: ValueAnimator? = null
    private var current: View? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_image_viewer_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter.setListener(adapterListener)
        adapter.set(viewModel.getInitial())
        (viewer.getChildAt(0) as? RecyclerView?)?.clipChildren = false
        viewer.adapter = adapter

    }


    private val adapterListener by lazy {
        object : ImageViewerAdapterListener {
            override fun onInit(view: View) {
                current = view
                init(view)
                container.changeToBackgroundColor(Color.BLACK)
            }

            override fun onDrag(view: PhotoView2, fraction: Float) {
                current = view
                container.updateBackgroundColor(fraction, Color.BLACK, Color.TRANSPARENT)
            }

            override fun onRestore(view: PhotoView2, fraction: Float) {
                current = view
                container.changeToBackgroundColor(Color.BLACK)
            }

            override fun onRelease(view: PhotoView2) {
                current = view
                release(view)

            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter.setListener(null)
        animator?.cancel()
    }

    private fun init(endView: View) {
        val startView: View? = viewModel.transform?.getOriginView(0)
        endView.doOnPreDraw {
            animator = ValueAnimator.ofFloat(0f, 1f).apply {
                duration = 200
                interpolator = DecelerateInterpolator()
                if (startView == null) {
                    addUpdateListener {
                        val fraction = it.animatedValue as Float
                        endView.alpha = fraction
                    }
                } else {
                    val w1 = startView.width
                    val w2 = endView.width
                    val h1 = startView.height
                    val h2 = endView.height
                    addUpdateListener {
                        val fraction = it.animatedValue as Float
                        endView.layoutParams = (endView.layoutParams as? ViewGroup.MarginLayoutParams)?.apply {
                            width = (w1 + (w2 - w1) * fraction).toInt()
                            height = (h1 + (h2 - h1) * fraction).toInt()
                            setMargins((startView.left * (1 - fraction)).toInt(), (startView.top * (1 - fraction)).toInt(), 0, 0)
                        }
                    }
                }
            }
            animator?.start()
        }
    }

    private fun release(endView: View) {
        // todo animating intercept

        container.changeToBackgroundColor(Color.TRANSPARENT)

        val startView: View? = viewModel.transform?.getOriginView(0)
            animator = ValueAnimator.ofFloat(1f, 0f).apply {
                duration = 200
                interpolator = DecelerateInterpolator()
                if (startView == null) {
                    addUpdateListener {
                        val fraction = it.animatedValue as Float
                        endView.alpha = fraction
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
                    addUpdateListener {
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
                    addListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator?) {
                            dismissAllowingStateLoss()
                        }
                    })
                }
            }
            animator?.start()
    }

    override fun onBackPressed() {
        current?.let { release(it) }
    }
}
