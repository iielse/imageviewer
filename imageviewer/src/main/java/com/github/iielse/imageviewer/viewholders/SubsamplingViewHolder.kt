package com.github.iielse.imageviewer.viewholders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.github.iielse.imageviewer.ImageViewerAdapterListener
import com.github.iielse.imageviewer.R
import com.github.iielse.imageviewer.adapter.ItemType
import com.github.iielse.imageviewer.core.Components.requireImageLoader
import com.github.iielse.imageviewer.core.Components.requireVHCustomizer
import com.github.iielse.imageviewer.core.Photo
import com.github.iielse.imageviewer.utils.Config
import com.github.iielse.imageviewer.widgets.SubsamplingScaleImageView2
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_imageviewer_subsampling.*

class SubsamplingViewHolder(override val containerView: View, callback: ImageViewerAdapterListener) : RecyclerView.ViewHolder(containerView), LayoutContainer {
    init {
        subsamplingView.setMinimumScaleType(Config.SUBSAMPLING_SCALE_TYPE)
        subsamplingView.setListener(object : SubsamplingScaleImageView2.Listener {
            override fun onDrag(view: SubsamplingScaleImageView2, fraction: Float) = callback.onDrag(this@SubsamplingViewHolder, view, fraction)
            override fun onRestore(view: SubsamplingScaleImageView2, fraction: Float) = callback.onRestore(this@SubsamplingViewHolder, view, fraction)
            override fun onRelease(view: SubsamplingScaleImageView2) = callback.onRelease(this@SubsamplingViewHolder, view)
        })
        requireVHCustomizer().initialize(ItemType.SUBSAMPLING, this)
    }

    fun bind(item: Photo) {
        subsamplingView.setTag(R.id.viewer_adapter_item_key, item.id())
        subsamplingView.setTag(R.id.viewer_adapter_item_data, item)
        subsamplingView.setTag(R.id.viewer_adapter_item_holder, this)
        requireVHCustomizer().bind(ItemType.SUBSAMPLING, item, this)
        requireImageLoader().load(subsamplingView, item, this)
    }
}


