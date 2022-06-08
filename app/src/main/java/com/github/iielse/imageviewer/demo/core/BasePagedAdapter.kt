package com.github.iielse.imageviewer.demo.core

import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.github.iielse.imageviewer.demo.R
import com.github.iielse.imageviewer.demo.utils.inflate
import java.util.*

const val ID_EMPTY = "id_empty"
const val ID_MORE_RETRY = "id_more_retry"
const val ID_ERROR = "id_error"
const val ID_MORE_LOADING = "id_more_loading" // 其实可以用 负数int类型的
const val ID_NO_MORE = "id_no_more"
const val PAGE_INITIAL = 1
const val ITEM_CLICKED = "item_clicked"
typealias AdapterCallback = (action: String, item: Any?) -> Unit

var itemTypeProvider = 1

data class Cell(
    val type: Int,
    val id: String,
    val extra: Any? = null
) {
    inline fun <reified T> data(): T? {
        return extra as? T?
    }
}


abstract class BasePagedAdapter : PagedListAdapter<Cell, RecyclerView.ViewHolder>(DIFF) {

    private var listener: AdapterCallback? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            BaseItemType.Error -> ErrorViewHolder(parent.inflate(R.layout.layout_recycler_error))
            BaseItemType.Empty -> EmptyViewHolder(parent.inflate(R.layout.layout_recycler_empty))
            BaseItemType.Loading -> MoreLoadingViewHolder(parent.inflate(R.layout.layout_recycler_loading))
            else -> UnknownViewHolder(View(parent.context).apply {
                layoutParams = ViewGroup.LayoutParams(0, 0)
            })
        }
    }

    fun setListener(callback: AdapterCallback?) {
        listener = callback
    }

    protected val callback: AdapterCallback = { item, action ->
        listener?.invoke(item, action)
    }

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        return item?.type ?: BaseItemType.Unknown
    }
}

private val DIFF = object : DiffUtil.ItemCallback<Cell>() {
    override fun areItemsTheSame(oldItem: Cell, newItem: Cell): Boolean {
        return newItem.type == oldItem.type && newItem.id == oldItem.id
    }

    override fun areContentsTheSame(oldItem: Cell, newItem: Cell): Boolean {
        return newItem.type == oldItem.type &&
                 Objects.equals(newItem.data(), oldItem.data())
    }
}

fun superProcessCell(itemTypeId: String): Cell? {
    return when (itemTypeId) {
        ID_EMPTY -> Cell(BaseItemType.Empty, ID_EMPTY)
        ID_ERROR -> Cell(BaseItemType.Error, ID_ERROR)
        ID_MORE_LOADING -> Cell(BaseItemType.Loading, ID_MORE_LOADING)
        ID_MORE_RETRY -> Cell(BaseItemType.Retry, ID_MORE_RETRY)
        ID_NO_MORE -> Cell(BaseItemType.NoMore, ID_NO_MORE)
        else -> null
    }
}

class MoreRetryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
class UnknownViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
class ErrorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
class MoreLoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
class NoMoreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
class EmptyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

object BaseItemType {
    const val Unknown = -1
    val Empty = itemTypeProvider++
    val Error = itemTypeProvider++
    val Loading = itemTypeProvider++
    val Retry = itemTypeProvider++
    val NoMore = itemTypeProvider++
    val SlideTryMore = itemTypeProvider++
}
