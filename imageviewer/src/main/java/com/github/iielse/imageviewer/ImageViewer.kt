package com.github.iielse.imageviewer

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.paging.PagedList
import com.github.iielse.imageviewer.core.PWrapper
import kotlinx.android.synthetic.main.imageviewer.view.*

class ImageViewer @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : ConstraintLayout(context, attrs, defStyleAttr) {
    private val adapter by lazy { ImageViewerAdapter() }

    init {
        LayoutInflater.from(context).inflate(R.layout.imageviewer, this)
        pager.adapter = adapter
    }

    fun set(dataList: PagedList<PWrapper>) {
        adapter.submitList(dataList)
    }
}