package com.github.iielse.imageviewer

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.github.iielse.imageviewer.utils.Config.OFFSCREEN_PAGE_LIMIT
import com.github.iielse.imageviewer.adapter.ImageViewerAdapter
import com.github.iielse.imageviewer.core.Components.requireInitKey
import com.github.iielse.imageviewer.core.Components.requireOverlayCustomizer
import com.github.iielse.imageviewer.core.Components.requireTransformer
import com.github.iielse.imageviewer.core.Components.requireViewerCallback
import com.github.iielse.imageviewer.utils.AnimHelper
import com.github.iielse.imageviewer.utils.Config
import com.github.iielse.imageviewer.utils.findViewWithKeyTag
import com.github.iielse.imageviewer.utils.log
import com.github.iielse.imageviewer.viewholders.PhotoViewHolder
import kotlinx.android.synthetic.main.fragment_image_viewer_dialog.*
import kotlinx.android.synthetic.main.item_imageviewer_photo.view.*
import kotlin.math.max
import kotlin.math.min

class ImageViewerDialogFragment : BaseDialogFragment() {
    private val events by lazy { ViewModelProvider(requireActivity()).get(ImageViewerActionViewModel::class.java) }
    private val viewModel by lazy { ViewModelProvider(this).get(ImageViewerViewModel::class.java) }
    private val userCallback by lazy { requireViewerCallback() }
    private val initKey by lazy { requireInitKey() }
    private val transformer by lazy { requireTransformer() }
    private val adapter by lazy { ImageViewerAdapter(initKey) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_image_viewer_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter.setListener(adapterListener)
        (viewer.getChildAt(0) as? RecyclerView?)?.let {
            it.clipChildren = false
            it.itemAnimator = null
        }
        viewer.orientation = Config.VIEWER_ORIENTATION
        viewer.registerOnPageChangeCallback(pagerCallback)
        viewer.offscreenPageLimit = OFFSCREEN_PAGE_LIMIT
        viewer.adapter = adapter

        overlayView.addView(requireOverlayCustomizer().provideView(overlayView))

        viewModel.dataList.observe(viewLifecycleOwner, Observer {
            log { "submitList ${it.size}" }
            adapter.submitList(it)
            viewer.setCurrentItem(it.indexOfFirst { it.id == initKey }, false)
        })

        events.actionEvent.observe(viewLifecycleOwner, Observer(::handle))
    }

    private fun handle(action: Pair<String, Any?>?) {
        when (action?.first) {
            ViewerActions.SET_CURRENT_ITEM -> viewer.currentItem = min(max(action.second as Int, 0), adapter.itemCount)
            ViewerActions.DISMISS -> onBackPressed()
        }
    }

    private val adapterListener by lazy {
        object : ImageViewerAdapterListener {
            override fun onInit(viewHolder: RecyclerView.ViewHolder) {
                when (viewHolder) {
                    is PhotoViewHolder -> {
                        AnimHelper.start(this@ImageViewerDialogFragment, transformer.getView(initKey), viewHolder.itemView.photoView)
                    }
                }
                background.changeToBackgroundColor(Config.VIEWER_BACKGROUND_COLOR)
                userCallback.onInit(viewHolder)
            }

            override fun onDrag(viewHolder: RecyclerView.ViewHolder, view: View, fraction: Float) {
                background.updateBackgroundColor(fraction, Config.VIEWER_BACKGROUND_COLOR, Color.TRANSPARENT)
                userCallback.onDrag(viewHolder, view, fraction)
            }

            override fun onRestore(viewHolder: RecyclerView.ViewHolder, view: View, fraction: Float) {
                background.changeToBackgroundColor(Config.VIEWER_BACKGROUND_COLOR)
                userCallback.onRestore(viewHolder, view, fraction)
            }

            override fun onRelease(viewHolder: RecyclerView.ViewHolder, view: View) {
                val startView = (view.getTag(R.id.viewer_adapter_item_key) as? Long?)?.let { transformer.getView(it) }
                AnimHelper.end(this@ImageViewerDialogFragment, startView, view)
                background.changeToBackgroundColor(Color.TRANSPARENT)
                userCallback.onRelease(viewHolder, view)
            }
        }
    }

    private val pagerCallback by lazy {
        object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrollStateChanged(state: Int) {
                userCallback.onPageScrollStateChanged(state)
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                userCallback.onPageScrolled(position, positionOffset, positionOffsetPixels)
            }

            override fun onPageSelected(position: Int) {
                userCallback.onPageSelected(position)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter.setListener(null)
        viewer.unregisterOnPageChangeCallback(pagerCallback)
    }

    override fun onBackPressed() {
        log { "onBackPressed ${viewer.currentItem}" }
        val currentKey = adapter.getItemId(viewer.currentItem)
        viewer.findViewWithKeyTag(R.id.viewer_adapter_item_key, currentKey)?.let { endView ->
            val startView = transformer.getView(currentKey)
            AnimHelper.end(this, startView, endView)
            background.changeToBackgroundColor(Color.TRANSPARENT)

            (endView.getTag(R.id.viewer_adapter_item_holder) as? RecyclerView.ViewHolder?)?.let {
                userCallback.onRelease(it, endView)
            }
        }
    }
}
