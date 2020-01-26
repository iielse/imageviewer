package com.github.iielse.imageviewer

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.github.iielse.imageviewer.utils.Config.OFFSCREEN_PAGE_LIMIT
import com.github.iielse.imageviewer.adapter.ImageViewerAdapter
import com.github.iielse.imageviewer.adapter.Item
import com.github.iielse.imageviewer.core.Components.requireImageLoader
import com.github.iielse.imageviewer.core.Components.requireInitKey
import com.github.iielse.imageviewer.core.Components.requireTransformer
import com.github.iielse.imageviewer.core.Photo
import com.github.iielse.imageviewer.utils.AnimHelper
import com.github.iielse.imageviewer.utils.findViewWithKeyTag
import com.github.iielse.imageviewer.utils.log
import com.github.iielse.imageviewer.widgets.PhotoView2
import kotlinx.android.synthetic.main.fragment_image_viewer_dialog.*

class ImageViewerDialogFragment : BaseDialogFragment() {
    private val viewModel by lazy { ViewModelProviders.of(this).get(ImageViewerViewModel::class.java) }
    private val initKey by lazy { requireInitKey() }
    private val imageLoader by lazy { requireImageLoader() }
    private val transformer by lazy { requireTransformer() }
    private val adapter by lazy { ImageViewerAdapter(initKey) }
    private var initView: View? = null

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
        viewer.registerOnPageChangeCallback(pageChangeCallback)
        viewer.offscreenPageLimit = OFFSCREEN_PAGE_LIMIT
        viewer.adapter = adapter

        viewModel.dataList.observe(this, Observer {
            log { "submitList ${it.size}" }
            adapter.submitList(it)
            viewer.setCurrentItem(it.indexOfFirst { it.id == initKey }, false)
        })
    }

    private val pageChangeCallback by lazy {
        object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                log { "onPageSelected $position ${viewer.currentItem}" }
                // try fix initView tag position
                (initView?.getTag(R.id.viewer_adapter_item_pos) as? Int?)?.let {
                    if (it != position) {
                        initView?.setTag(R.id.viewer_adapter_item_pos, position)
                        initView = null
                    }
                }
            }

        }
    }

    private val adapterListener by lazy {
        object : ImageViewerAdapterListener {
            override fun onInit(view: PhotoView2) {
                log { "onInit $view" }
                initView = view
                AnimHelper.start(this@ImageViewerDialogFragment, transformer.getView(initKey), view)
                container.changeToBackgroundColor(Color.BLACK)
            }

            override fun onDrag(view: PhotoView2, fraction: Float) {
                container.updateBackgroundColor(fraction, Color.BLACK, Color.TRANSPARENT)
            }

            override fun onRestore(view: PhotoView2, fraction: Float) {
                container.changeToBackgroundColor(Color.BLACK)
            }

            override fun onRelease(view: PhotoView2) {
                val startView = (view.getTag(R.id.viewer_adapter_item_data) as? Photo?)?.id()?.let { transformer.getView(it) }
                AnimHelper.end(this@ImageViewerDialogFragment, startView, view)
                container.changeToBackgroundColor(Color.TRANSPARENT)
            }

            override fun onLoad(view: ImageView, item: Photo) {
                imageLoader.load(view, item)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewer.unregisterOnPageChangeCallback(pageChangeCallback)
        adapter.setListener(null)
    }

    override fun onBackPressed() {
        log { "onBackPressed ${viewer.currentItem}" }
        viewer.findViewWithKeyTag(R.id.viewer_adapter_item_pos, viewer.currentItem)?.let {
            val startView = (it.getTag(R.id.viewer_adapter_item_data) as? Photo?)?.id()?.let { transformer.getView(it) }
            AnimHelper.end(this, startView, it)
            container.changeToBackgroundColor(Color.TRANSPARENT)
        }
    }
}
