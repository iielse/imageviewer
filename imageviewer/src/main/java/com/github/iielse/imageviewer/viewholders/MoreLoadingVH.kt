package com.github.iielse.imageviewer.viewholders

import android.view.View
import androidx.recyclerview.widget.AdapterListUpdateCallback
import androidx.recyclerview.widget.RecyclerView
import com.github.iielse.imageviewer.ImageViewerAdapterListener


class MoreLoadingVH(itemView: View, private val callback: ImageViewerAdapterListener) : RecyclerView.ViewHolder(itemView) {
    fun bind() {
        callback.onLoadMore()
    }
}