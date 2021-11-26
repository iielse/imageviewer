package com.github.iielse.imageviewer.demo.business

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.iielse.imageviewer.demo.core.BasePagedAdapter
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
}
