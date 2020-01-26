package com.github.iielse.imageviewer.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.github.iielse.imageviewer.*
import com.github.iielse.imageviewer.core.Photo
import com.github.iielse.imageviewer.utils.Config.INVALID
import com.github.iielse.imageviewer.utils.inflate
import com.github.iielse.imageviewer.utils.log

import com.github.iielse.imageviewer.viewholders.MoreLoadingVH
import com.github.iielse.imageviewer.viewholders.MoreRetryVH
import com.github.iielse.imageviewer.viewholders.NoMoreVH
import com.github.iielse.imageviewer.viewholders.PhotoViewHolder
import com.github.iielse.imageviewer.viewholders.SubsamplingViewHolder
import com.github.iielse.imageviewer.widgets.PhotoView2
import kotlinx.android.synthetic.main.item_imageviewer_photo.view.*
import java.util.*

class ImageViewerAdapter(initKey: Int) : PagedListAdapter<Item, RecyclerView.ViewHolder>(diff) {
    private var listener: ImageViewerAdapterListener? = null
    private var key = initKey

    fun setListener(callback: ImageViewerAdapterListener?) {
        listener = callback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ItemType.PHOTO -> PhotoViewHolder(parent.inflate(R.layout.item_imageviewer_photo), callback)
            ItemType.SUBSAMPLING -> SubsamplingViewHolder(parent.inflate(R.layout.item_imageviewer_subsampling))
            ItemType.MORE_LOADING -> MoreLoadingVH(parent.inflate(R.layout.item_imageviewer_more_loading))
            ItemType.MORE_RETRY -> MoreRetryVH(parent.inflate(R.layout.item_imageviewer_more_retry))
            else -> NoMoreVH(parent.inflate(R.layout.item_imageviewer_no_more))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        log { "onBindViewHolder $key $position $item" }
        when (holder) {
            is PhotoViewHolder -> item?.extra<Photo>()?.let { holder.bind(it, position) }
            is SubsamplingViewHolder -> item?.extra<Photo>()?.let { holder.bind(it) }
        }

        if (item?.id == key) {
            listener?.onInit(holder.itemView.photoView)
            key = INVALID
        }

    }

    override fun getItemViewType(position: Int) = getItem(position)?.type ?: ItemType.UNKNOWN
    private val callback: ImageViewerAdapterListener = object : ImageViewerAdapterListener {
        override fun onInit(view: PhotoView2) {
            listener?.onInit(view)
        }

        override fun onDrag(itemView: View, view: PhotoView2, fraction: Float) {
            listener?.onDrag(itemView, view, fraction)
        }

        override fun onRelease(itemView: View, view: PhotoView2) {
            listener?.onRelease(itemView, view)
        }

        override fun onRestore(itemView: View, view: PhotoView2, fraction: Float) {
            listener?.onRestore(itemView, view, fraction)
        }

        override fun onLoad(view: ImageView, item: Photo) {
            listener?.onLoad(view, item)
        }
    }
}

private val diff = object : DiffUtil.ItemCallback<Item>() {
    override fun areItemsTheSame(
            oldItem: Item,
            newItem: Item
    ): Boolean {
        return newItem.type == oldItem.type && newItem.id == oldItem.id
    }

    override fun areContentsTheSame(
            oldItem: Item,
            newItem: Item
    ): Boolean {
        return newItem.type == oldItem.type && newItem.id == oldItem.id
                && Objects.equals(newItem.extra, oldItem.extra)
    }

}
