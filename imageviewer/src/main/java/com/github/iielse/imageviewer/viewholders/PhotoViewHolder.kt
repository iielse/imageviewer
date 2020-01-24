package com.github.iielse.imageviewer.viewholders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.github.iielse.imageviewer.Photo
import com.github.iielse.imageviewer.PhotoView2
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_imageviewer_photo.*

class PhotoViewHolder(override val containerView: View, callback: PhotoView2.Listener) : RecyclerView.ViewHolder(containerView), LayoutContainer {
    init {
        photoView.setListener(callback)
    }

    fun bind(item: Photo) {
        Glide.with(photoView).load(item.url).into(photoView)
    }
}
