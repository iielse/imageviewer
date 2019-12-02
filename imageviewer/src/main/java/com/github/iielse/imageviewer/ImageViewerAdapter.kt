package com.github.iielse.imageviewer

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.iielse.imageviewer.core.*
import com.github.iielse.imageviewer.model.Photo
import com.github.iielse.imageviewer.viewholders.PhotoViewHolder
import com.github.iielse.imageviewer.viewholders.SubsamplingViewHolder

object ItemType {
    val PHOTO by lazy { itemTypeProvider++ }
    val SUBSAMPLING by lazy { itemTypeProvider++ }
}

class ImageViewerAdapter : PAdapter() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ItemType.PHOTO -> PhotoViewHolder(parent.inflate(R.layout.item_imageviewer_photo))
            ItemType.SUBSAMPLING -> SubsamplingViewHolder(parent.inflate(R.layout.item_imageviewer_subsampling))
            else -> super.onCreateViewHolder(parent, viewType)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (holder) {
            is PhotoViewHolder -> item?.extra<Photo>()?.let { holder.bind(it) }
            is SubsamplingViewHolder -> item?.extra<Photo>()?.let { holder.bind(it) }
        }
    }
}

