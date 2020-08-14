package com.github.iielse.imageviewer.viewholders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.github.iielse.imageviewer.ImageViewerAdapterListener
import com.github.iielse.imageviewer.R
import com.github.iielse.imageviewer.adapter.ItemType
import com.github.iielse.imageviewer.core.Components.requireImageLoader
import com.github.iielse.imageviewer.core.Components.requireVHCustomizer
import com.github.iielse.imageviewer.core.Photo
import com.github.iielse.imageviewer.widgets.video.ExoVideoView2
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_imageviewer_video.*

class VideoViewHolder(override val containerView: View, callback: ImageViewerAdapterListener) : RecyclerView.ViewHolder(containerView), LayoutContainer {
    init {
        videoView.addListener(object : ExoVideoView2.Listener {
            override fun onDrag(view: ExoVideoView2, fraction: Float) = callback.onDrag(this@VideoViewHolder, view, fraction)
            override fun onRestore(view: ExoVideoView2, fraction: Float) = callback.onRestore(this@VideoViewHolder, view, fraction)
            override fun onRelease(view: ExoVideoView2) = callback.onRelease(this@VideoViewHolder, view)
        })
        requireVHCustomizer().initialize(ItemType.VIDEO, this)
    }

    fun bind(item: Photo) {
        videoView.setTag(R.id.viewer_adapter_item_key, item.id())
        videoView.setTag(R.id.viewer_adapter_item_data, item)
        videoView.setTag(R.id.viewer_adapter_item_holder, this)
        requireVHCustomizer().bind(ItemType.VIDEO, item, this)
        requireImageLoader().load(videoView, item, this)
    }
}