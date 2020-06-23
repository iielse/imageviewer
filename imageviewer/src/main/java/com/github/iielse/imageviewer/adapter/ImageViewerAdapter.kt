package com.github.iielse.imageviewer.adapter

import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.NO_ID
import com.github.iielse.imageviewer.ImageViewerAdapterListener
import com.github.iielse.imageviewer.R
import com.github.iielse.imageviewer.core.Photo
import com.github.iielse.imageviewer.utils.inflate
import com.github.iielse.imageviewer.utils.log
import com.github.iielse.imageviewer.viewholders.PhotoViewHolder
import com.github.iielse.imageviewer.viewholders.SubsamplingViewHolder
import com.github.iielse.imageviewer.viewholders.UnknownViewHolder
import com.github.iielse.imageviewer.viewholders.VideoViewHolder
import java.util.*

class ImageViewerAdapter(initKey: Long) : PagedListAdapter<Item, RecyclerView.ViewHolder>(diff) {
    private var listener: ImageViewerAdapterListener? = null
    private var key = initKey

    fun setListener(callback: ImageViewerAdapterListener?) {
        listener = callback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ItemType.PHOTO -> PhotoViewHolder(parent.inflate(R.layout.item_imageviewer_photo), callback)
            ItemType.SUBSAMPLING -> SubsamplingViewHolder(parent.inflate(R.layout.item_imageviewer_subsampling), callback)
            ItemType.VIDEO -> VideoViewHolder(parent.inflate(R.layout.item_imageviewer_video), callback)
            else -> UnknownViewHolder(View(parent.context))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (holder) {
            is PhotoViewHolder -> item?.extra<Photo>()?.let { holder.bind(it) }
            is SubsamplingViewHolder -> item?.extra<Photo>()?.let { holder.bind(it) }
            is VideoViewHolder -> item?.extra<Photo>()?.let { holder.bind(it) }
        }

        if (item?.id == key) {
            listener?.onInit(holder)
            key = NO_ID
        }
    }

    override fun getItemId(position: Int): Long = provideItem(position)?.id ?: NO_ID
    override fun getItemViewType(position: Int) = provideItem(position)?.type ?: ItemType.UNKNOWN
    private val callback: ImageViewerAdapterListener = object : ImageViewerAdapterListener {
        override fun onInit(viewHolder: RecyclerView.ViewHolder) {
            listener?.onInit(viewHolder)
        }

        override fun onDrag(viewHolder: RecyclerView.ViewHolder, view: View, fraction: Float) {
            listener?.onDrag(viewHolder, view, fraction)
        }

        override fun onRelease(viewHolder: RecyclerView.ViewHolder, view: View) {
            listener?.onRelease(viewHolder, view)
        }

        override fun onRestore(viewHolder: RecyclerView.ViewHolder, view: View, fraction: Float) {
            listener?.onRestore(viewHolder, view, fraction)
        }
    }

    private fun provideItem(position: Int) = try {
        getItem(position) // IndexOutOfBoundsException Item count is zero, getItem() call is invalid
    } catch (e: Exception) {
        null
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
