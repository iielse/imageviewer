package com.github.iielse.imageviewer

import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.github.iielse.imageviewer.core.*
import java.util.*

class ImageViewerAdapter : PagedListAdapter<PWrapper, RecyclerView.ViewHolder>(DIFF) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

private val DIFF = object : DiffUtil.ItemCallback<PWrapper>() {
    override fun areItemsTheSame(oldItem: PWrapper, newItem: PWrapper): Boolean {
        return newItem.type == oldItem.type && newItem.id == oldItem.id
    }

    override fun areContentsTheSame(oldItem: PWrapper, newItem: PWrapper): Boolean {
        return newItem.type == oldItem.type && Objects.equals(newItem.get(), oldItem.get())
    }
}

private var itemTypeProvider = 1

enum class ItemType(val value: Int) {
    EmptyPlaceHolder(itemTypeProvider++),
    ErrorPlaceHolder(itemTypeProvider++),
    LoadingPlaceHolder(itemTypeProvider++),
    RetryPlaceHolder(itemTypeProvider++),
    NoMorePlaceHolder(itemTypeProvider++),
    Photo(itemTypeProvider++), ;

    fun lastItemType(): Int {
        return itemTypeProvider
    }
}

fun transformDefaultPWrapper(itemTypeId: String): PWrapper? {
    return when (itemTypeId) {
        ID_EMPTY -> PWrapper(ItemType.EmptyPlaceHolder.value, ID_EMPTY)
        ID_ERROR -> PWrapper(ItemType.ErrorPlaceHolder.value, ID_ERROR)
        ID_MORE_LOADING -> PWrapper(ItemType.LoadingPlaceHolder.value, ID_MORE_LOADING)
        ID_MORE_RETRY -> PWrapper(ItemType.RetryPlaceHolder.value, ID_MORE_RETRY)
        ID_NO_MORE -> PWrapper(ItemType.NoMorePlaceHolder.value, ID_NO_MORE)
        else -> null
    }
}


