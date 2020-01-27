package com.github.iielse.imageviewer.viewholders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.github.iielse.imageviewer.ImageViewerAdapterListener
import com.github.iielse.imageviewer.R
import com.github.iielse.imageviewer.core.Components.requireImageLoader
import com.github.iielse.imageviewer.core.Photo
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_imageviewer_photo.*

class PhotoViewHolder(override val containerView: View, callback: ImageViewerAdapterListener) : RecyclerView.ViewHolder(containerView), LayoutContainer {
    init {
        photoView.setListener(callback)
    }

    fun bind(item: Photo) {
        photoView.setTag(R.id.viewer_adapter_item_key, item.id())
        requireImageLoader().load(photoView, item)
    }
}