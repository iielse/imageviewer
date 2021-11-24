package com.github.iielse.imageviewer.demo.business

import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.github.iielse.imageviewer.demo.core.AdapterCallback
import com.github.iielse.imageviewer.demo.core.BasePagedAdapter
import com.github.iielse.imageviewer.demo.core.viewer.TransitionViewsRef
import com.github.iielse.imageviewer.demo.core.viewer.TransitionViewsRef.KEY_MAIN
import com.github.iielse.imageviewer.demo.data.MyData

class TestDataAdapter : BasePagedAdapter() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ItemType.TestData -> TestDataViewHolder(parent, callback)
            else -> super.onCreateViewHolder(parent, viewType)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (holder) {
            is TestDataViewHolder -> item?.data<MyData>()?.let { holder.bind(it, position) }
        }
    }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        super.onViewAttachedToWindow(holder)
        if (holder is TestDataViewHolder) {
            (holder.itemView.tag as? MyData?)?.let {
                TransitionViewsRef.provideTransitionViewsRef(KEY_MAIN).put(it.id, holder.binding.imageView)
            }
        }
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        if (holder is TestDataViewHolder) {
            (holder.itemView.tag as? MyData?)?.let {
                TransitionViewsRef.provideTransitionViewsRef(KEY_MAIN).remove(it.id)
            }
        }
    }
}
