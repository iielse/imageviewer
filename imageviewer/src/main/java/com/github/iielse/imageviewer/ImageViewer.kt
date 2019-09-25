package com.github.iielse.imageviewer

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.paging.PagedList
import androidx.viewpager2.widget.ViewPager2
import com.github.iielse.imageviewer.core.PWrapper

class ImageViewer @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : ConstraintLayout(context, attrs, defStyleAttr) {
    private val viewPager by lazy { ViewPager2(context, attrs, defStyleAttr) }
    private val overlayView by lazy { ConstraintLayout(context, attrs, defStyleAttr) }
    private val adapter by lazy { ImageViewerAdapter() }

    init {
        addView(viewPager)
        addView(overlayView)
        viewPager.adapter = adapter
    }

    fun set(dataList: PagedList<PWrapper>) {
        adapter.submitList(dataList)
    }
}