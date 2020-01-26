package com.github.iielse.imageviewer.viewholders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.github.iielse.imageviewer.ImageViewerAdapterListener
import com.github.iielse.imageviewer.Photo
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_imageviewer_photo.*

class PhotoViewHolder(override val containerView: View, private val callback: ImageViewerAdapterListener) : RecyclerView.ViewHolder(containerView), LayoutContainer {
    init {
        photoView.setListener(callback)
    }

    fun bind(item: Photo) {
        callback.onLoad(photoView, item)
    }
}