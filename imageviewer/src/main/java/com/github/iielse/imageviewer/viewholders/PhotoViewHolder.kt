package com.github.iielse.imageviewer.viewholders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.github.iielse.imageviewer.ImageViewerAdapterListener
import com.github.iielse.imageviewer.R
import com.github.iielse.imageviewer.core.Photo
import com.github.iielse.imageviewer.widgets.PhotoView2
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_imageviewer_photo.*

class PhotoViewHolder(override val containerView: View, private val callback: ImageViewerAdapterListener) : RecyclerView.ViewHolder(containerView), LayoutContainer {
    init {
        photoView.setListener(object : PhotoView2.Listener {
            override fun onDrag(view: PhotoView2, fraction: Float) {
                callback.onDrag(itemView, view, fraction)
            }

            override fun onRestore(view: PhotoView2, fraction: Float) {
                callback.onRestore(itemView, view, fraction)

            }

            override fun onRelease(view: PhotoView2) {
                callback.onRelease(itemView, view)
            }
        })
    }

    fun bind(item: Photo, pos: Int) {

        photoView.setTag(R.id.viewer_adapter_item_pos, pos)
        photoView.setTag(R.id.viewer_adapter_item_data, item)

        callback.onLoad(photoView, item)
    }
}