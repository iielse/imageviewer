package com.github.iielse.imageviewer

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.iielse.imageviewer.Config.POS_INVALID
import kotlinx.android.synthetic.main.item_imageviewer_photo.view.*

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

class MoreLoadingVH(itemView: View) : RecyclerView.ViewHolder(itemView)
class MoreRetryVH(itemView: View) : RecyclerView.ViewHolder(itemView)
class NoMoreVH(itemView: View) : RecyclerView.ViewHolder(itemView)

const val ITEM_DRAG = "adapter_item_drag"
const val ITEM_INIT = "adapter_item_init"

class ImageViewerAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val dataList = mutableListOf<Item>()
    private var listener: ImageViewerAdapterListener? = null
    private var initPos = 0

    fun setListener(callback: ImageViewerAdapterListener?) {
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
            listener?.onInit(holder.itemView.photoView)
            initPos = POS_INVALID
        }
    }

    override fun getItemViewType(position: Int) = dataList[position].type
    override fun getItemCount() = dataList.size
    private val callback: ImageViewerAdapterListener = object : ImageViewerAdapterListener {
        override fun onInit(view: View) {
            listener?.onInit(view)
        }

        override fun onDrag(view: PhotoView2, fraction: Float) {
            listener?.onDrag(view, fraction)
        }

        override fun onRelease(view: PhotoView2) {
            listener?.onRelease(view)
        }

        override fun onRestore(view: PhotoView2, fraction: Float) {
            listener?.onRestore(view, fraction)
        }
    }
}

