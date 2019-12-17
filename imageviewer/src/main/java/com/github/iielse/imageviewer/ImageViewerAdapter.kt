package com.github.iielse.imageviewer

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.iielse.imageviewer.Config.POS_INVALID
import com.github.iielse.imageviewer.core.*
import com.github.iielse.imageviewer.model.Photo
import com.github.iielse.imageviewer.viewholders.PhotoViewHolder
import com.github.iielse.imageviewer.viewholders.SubsamplingViewHolder

data class Item(
        val type: Int,
        val extra: Any? = null
) {
    inline fun <reified T> extra(): T? {
        return extra as? T?
    }
}

object ItemType {
    var itemTypeProvider = 1
    val MORE_LOADING = itemTypeProvider++
    val MORE_RETRY = itemTypeProvider++
    val NO_MORE = itemTypeProvider++
    val PHOTO = itemTypeProvider++
    val SUBSAMPLING = itemTypeProvider++
}

const val ITEM_DRAG = "adapter_item_drag"
const val ITEM_INIT = "adapter_item_init"

class ImageViewerAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val dataList = mutableListOf<Item>()
    private var listener: AdapterCallback? = null
    private var initPos = 0

    fun setListener(callback: AdapterCallback?) {
        listener = callback
    }

    fun set(sourceList: List<Photo>, initPos: Int = 0, fetchOver: Boolean = false) {
        val lastSize = dataList.size
        dataList.clear()
        notifyItemRangeRemoved(0, lastSize)
        dataList.addAll(sourceList.map { Item(ItemType.PHOTO, it) })
        notifyItemRangeInserted(0, sourceList.size)
        val photoSize = dataList.size
        dataList.add(Item(if (fetchOver) ItemType.NO_MORE else ItemType.MORE_LOADING))
        notifyItemInserted(photoSize - 1)
    }

    fun append(sourceList: List<Photo>, fetchOver: Boolean = false) {

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
        val item = dataList[position]
        when (holder) {
            is PhotoViewHolder -> item.extra<Photo>()?.let { holder.bind(it) }
            is SubsamplingViewHolder -> item.extra<Photo>()?.let { holder.bind(it) }
        }

        if (position == initPos) {
            listener?.invoke(ITEM_INIT, holder.itemView)
            initPos = POS_INVALID
        }
    }

    private val callback: AdapterCallback = { item, action ->
        listener?.invoke(item, action)
    }

    override fun getItemViewType(position: Int): Int {
        return dataList[position].type
    }


    override fun getItemCount() = dataList.size
}

