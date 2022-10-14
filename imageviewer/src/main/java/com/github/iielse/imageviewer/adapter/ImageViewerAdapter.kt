package com.github.iielse.imageviewer.adapter

import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.NO_ID
import com.github.iielse.imageviewer.ImageViewerAdapterListener
import com.github.iielse.imageviewer.core.Photo
import com.github.iielse.imageviewer.viewholders.PhotoViewHolder
import com.github.iielse.imageviewer.viewholders.SubsamplingViewHolder
import com.github.iielse.imageviewer.viewholders.UnknownViewHolder
import com.github.iielse.imageviewer.viewholders.VideoViewHolder
import java.util.*

class ImageViewerAdapter(initKey: Long) : PagingDataAdapter<Photo, RecyclerView.ViewHolder>(diff) {
    private var listener: ImageViewerAdapterListener? = null
    private var key = initKey

    fun setListener(callback: ImageViewerAdapterListener?) {
        listener = callback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ItemType.PHOTO -> PhotoViewHolder(parent, callback)
            ItemType.SUBSAMPLING -> SubsamplingViewHolder(parent, callback)
            ItemType.VIDEO -> VideoViewHolder(parent, callback)
            else -> UnknownViewHolder(View(parent.context))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = provideItem(position)
        when (holder) {
            is PhotoViewHolder -> item?.let { holder.bind(it) }
            is SubsamplingViewHolder -> item?.let { holder.bind(it) }
            is VideoViewHolder -> item?.let { holder.bind(it) }
        }
        if (item?.id() == key) {
            listener?.onInit(holder, position)
            key = NO_ID
        }
    }

    override fun getItemViewType(position: Int) = provideItem(position)?.itemType() ?: ItemType.UNKNOWN
    private val callback: ImageViewerAdapterListener = object : ImageViewerAdapterListener {
        override fun onInit(viewHolder: RecyclerView.ViewHolder, position: Int) {
            listener?.onInit(viewHolder, position)
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
        // Fatal Exception: java.util.ConcurrentModificationException
        // IndexOutOfBoundsException Item count is zero, getItem() call is invalid
        getItem(position)
    } catch (e: Throwable) {
        null
    }
}

private val diff
    get() = object : DiffUtil.ItemCallback<Photo>() {
        override fun areItemsTheSame(
            oldItem: Photo,
            newItem: Photo
        ): Boolean {
            return newItem.itemType() == oldItem.itemType() && newItem.id() == oldItem.id()
        }

        override fun areContentsTheSame(
            oldItem: Photo,
            newItem: Photo
        ): Boolean {
            return newItem.itemType() == oldItem.itemType() && newItem.id() == oldItem.id()
                    && Objects.equals(newItem.extra(), oldItem.extra())
        }
    }
