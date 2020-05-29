package com.github.iielse.imageviewer.demo.business

import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.github.iielse.imageviewer.demo.R
import com.github.iielse.imageviewer.demo.core.AdapterCallback
import com.github.iielse.imageviewer.demo.core.viewer.TransitionViewsRef
import com.github.iielse.imageviewer.demo.core.viewer.TransitionViewsRef.KEY_MAIN
import com.github.iielse.imageviewer.demo.data.MyData
import com.github.iielse.imageviewer.utils.inflate
import com.github.iielse.imageviewer.utils.log
import kotlinx.android.synthetic.main.item_image.*
import java.util.*

class TestDataAdapter : PagedListAdapter<MyData, RecyclerView.ViewHolder>(provideDiffer()) {
    private var callback: AdapterCallback? = null

    fun setListener(callback: AdapterCallback? = null) {
        this.callback = callback
    }

    private val listener = object : AdapterCallback {
        override fun invoke(action: String, item: Any?) {
            callback?.invoke(action, item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return TestDataViewHolder(parent.inflate(R.layout.item_image), listener)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (holder) {
            is TestDataViewHolder -> item?.let { holder.bind(it, position) }
        }
    }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        super.onViewAttachedToWindow(holder)
        if (holder is TestDataViewHolder) {
            (holder.itemView.tag as? MyData?)?.let {
                log { "DataViewHolder onViewAttachedToWindow ${it.id}" }
                TransitionViewsRef.provideTransitionViewsRef(KEY_MAIN).put(it.id, holder.imageView)
            }
        }
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        if (holder is TestDataViewHolder) {
            (holder.itemView.tag as? MyData?)?.let {
                log { "DataViewHolder onViewDetachedFromWindow ${it.id}" }
                TransitionViewsRef.provideTransitionViewsRef(KEY_MAIN).remove(it.id)
            }
        }
    }
}

private fun provideDiffer() = object : DiffUtil.ItemCallback<MyData>() {
    override fun areItemsTheSame(
            oldItem: MyData,
            newItem: MyData
    ): Boolean {
        return newItem.id == oldItem.id
    }

    override fun areContentsTheSame(
            oldItem: MyData,
            newItem: MyData
    ): Boolean {
        return newItem.id == oldItem.id
                && Objects.equals(newItem.url, oldItem.url)
                && Objects.equals(newItem.desc, oldItem.desc)
    }
}
