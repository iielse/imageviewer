package com.github.iielse.imageviewer.core

import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.github.iielse.imageviewer.R
import java.util.*

const val ID_UNKNOWN = "id_unknown"
const val ID_MORE_LOADING = "id_more_loading"
const val ID_MORE_RETRY = "id_more_retry"
const val ID_NO_MORE = "id_no_more"

const val ITEM_CLICKED = "adapter_item_clicked"

var itemTypeProvider = 1

object ItemType {
    val UNKNOWN by lazy { -1 }
    val MORE_LOADING by lazy { itemTypeProvider++ }
    val MORE_RETRY by lazy { itemTypeProvider++ }
    val NO_MORE by lazy { itemTypeProvider++ }
}

data class PWrapper(
        val type: Int,
        val id: String,
        val extra: Any? = null
) {
    inline fun <reified T> extra(): T? {
        return extra as? T?
    }
}

typealias AdapterCallback = (action: String, item: Any?) -> Unit

fun transformDefaultPWrapper(itemTypeId: String): PWrapper? {
    return when (itemTypeId) {
        ID_UNKNOWN -> PWrapper(ItemType.UNKNOWN, ID_UNKNOWN)
        ID_MORE_LOADING -> PWrapper(ItemType.MORE_LOADING, ID_MORE_LOADING)
        ID_MORE_RETRY -> PWrapper(ItemType.MORE_RETRY, ID_MORE_RETRY)
        ID_NO_MORE -> PWrapper(ItemType.NO_MORE, ID_NO_MORE)
        else -> null
    }
}

abstract class PAdapter : PagedListAdapter<PWrapper, RecyclerView.ViewHolder>(DIFF) {
    private var listener: AdapterCallback? = null

    fun setListener(callback: AdapterCallback?) {
        listener = callback
    }

    protected val callback: AdapterCallback = { item, action ->
        listener?.invoke(item, action)
    }

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        return item?.type ?: ItemType.UNKNOWN
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ItemType.MORE_LOADING -> MoreLoadingVH(parent.inflate(R.layout.item_imageviewer_more_loading))
            ItemType.MORE_RETRY -> MoreRetryVH(parent.inflate(R.layout.item_imageviewer_more_retry))
            ItemType.NO_MORE -> NoMoreVH(View(parent.context).apply { layoutParams = ViewGroup.LayoutParams(0, 0) })
            else -> UnknownVH(View(parent.context).apply { layoutParams = ViewGroup.LayoutParams(0, 0) })
        }
    }
}

class UnknownVH(itemView: View) : RecyclerView.ViewHolder(itemView)
class MoreLoadingVH(itemView: View) : RecyclerView.ViewHolder(itemView)
class MoreRetryVH(itemView: View) : RecyclerView.ViewHolder(itemView)
class NoMoreVH(itemView: View) : RecyclerView.ViewHolder(itemView)

private val DIFF = object : DiffUtil.ItemCallback<PWrapper>() {
    override fun areItemsTheSame(oldItem: PWrapper, newItem: PWrapper): Boolean {
        return newItem.type == oldItem.type && newItem.id == oldItem.id
    }

    override fun areContentsTheSame(oldItem: PWrapper, newItem: PWrapper): Boolean {
        return newItem.type == oldItem.type && Objects.equals(newItem.extra(), oldItem.extra())
    }
}


