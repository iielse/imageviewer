package com.github.iielse.imageviewer

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.github.iielse.imageviewer.utils.Config.OFFSCREEN_PAGE_LIMIT
import com.github.iielse.imageviewer.adapter.ImageViewerAdapter
import com.github.iielse.imageviewer.core.Components.requireInitKey
import com.github.iielse.imageviewer.core.Components.requireTransformer
import com.github.iielse.imageviewer.utils.AnimHelper
import com.github.iielse.imageviewer.utils.findViewWithKeyTag
import com.github.iielse.imageviewer.utils.log
import com.github.iielse.imageviewer.widgets.PhotoView2
import kotlinx.android.synthetic.main.fragment_image_viewer_dialog.*

class ImageViewerDialogFragment : BaseDialogFragment() {
    private val viewModel by lazy { ViewModelProviders.of(this).get(ImageViewerViewModel::class.java) }
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
        viewer.offscreenPageLimit = OFFSCREEN_PAGE_LIMIT
        viewer.adapter = adapter

        viewModel.dataList.observe(this, Observer {
            log { "submitList ${it.size}" }
            adapter.submitList(it)
            viewer.setCurrentItem(it.indexOfFirst { it.id == initKey }, false)
        })
    }

    private val adapterListener by lazy {
        object : ImageViewerAdapterListener {
            override fun onInit(view: PhotoView2) {
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
                val startView = (view.getTag(R.id.viewer_adapter_item_key) as? Long?)?.let { transformer.getView(it) }
                AnimHelper.end(this@ImageViewerDialogFragment, startView, view)
                container.changeToBackgroundColor(Color.TRANSPARENT)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter.setListener(null)
    }

    override fun onBackPressed() {
        log { "onBackPressed ${viewer.currentItem}" }
        val currentKey = adapter.getItemId(viewer.currentItem)
        viewer.findViewWithKeyTag(R.id.viewer_adapter_item_key, currentKey)?.let {
            val startView = transformer.getView(currentKey)
            AnimHelper.end(this, startView, it)
            container.changeToBackgroundColor(Color.TRANSPARENT)
        }
    }
}
