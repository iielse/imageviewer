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
import android.widget.ImageView
import androidx.core.view.doOnPreDraw
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.github.iielse.imageviewer.adapter.ImageViewerAdapter
import com.github.iielse.imageviewer.datasource.Components
import com.github.iielse.imageviewer.datasource.Components.requireImageLoader
import com.github.iielse.imageviewer.datasource.Components.requireInitialPosition
import com.github.iielse.imageviewer.datasource.Components.requireTransformer
import kotlinx.android.synthetic.main.fragment_image_viewer_dialog.*

class ImageViewerDialogFragment : BaseDialogFragment() {
    private val viewModel by lazy { ViewModelProviders.of(requireActivity()).get(ImageViewerViewModel::class.java) }
    private val initialPosition by lazy { requireInitialPosition() }
    private val imageLoader by lazy { requireImageLoader() }
    private val transformer by lazy { requireTransformer() }
    private val adapter by lazy { ImageViewerAdapter(initialPosition) }
    private var animator: ValueAnimator? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_image_viewer_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter.setListener(adapterListener)
        (viewer.getChildAt(0) as? RecyclerView?)?.clipChildren = false
        viewer.adapter = adapter
        viewer.setCurrentItem(initialPosition, false)

        viewModel.dataList.observe(this, Observer {
            adapter.submitList(it)
        })
    }

    private val adapterListener by lazy {
        object : ImageViewerAdapterListener {
            override fun onInit(view: View) {
                init(view)
                container.changeToBackgroundColor(Color.BLACK)
            }

            override fun onDrag(view: PhotoView2, fraction: Float) {
                container.updateBackgroundColor(fraction, Color.BLACK, Color.TRANSPARENT)
            }

            override fun onRestore(view: PhotoView2, fraction: Float) {
                container.changeToBackgroundColor(Color.BLACK)
            }

            override fun onRelease(view: PhotoView2) {
                release(view)
            }

            override fun onLoad(view: ImageView, item: Photo) {
                imageLoader.load(view, item)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter.setListener(null)
        animator?.cancel()
        viewModel.release()
        Components.release()
    }

    private fun init(endView: View) {
        val startView: View? = transformer?.getView(0)
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
        val startView: View? = transformer?.getView(viewer.currentItem)
        log { "viewer fragment release ${viewer.currentItem} $startView" }
        animator = ValueAnimator.ofFloat(1f, 0f).apply {
            duration = 200
            interpolator = DecelerateInterpolator()
            if (startView == null) {
                addUpdateListener {

                    val fraction = it.animatedValue as Float
                    endView.alpha = fraction
                    endView.scaleX = 1 + (1 - fraction) * 0.4f
                    endView.scaleY = 1 + (1 - fraction) * 0.4f
                    log { "release update $fraction " }
                }
                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        dismissAllowingStateLoss()
                    }
                })
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
        viewer.findViewWithKeyTag(R.id.viewer_adapter_item, viewer.currentItem)?.let { release(it) }
    }
}
