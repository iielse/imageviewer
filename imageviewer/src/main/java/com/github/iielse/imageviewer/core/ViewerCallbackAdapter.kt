package com.github.iielse.imageviewer.core

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.github.iielse.imageviewer.ImageViewerAdapterListener

open class ViewerCallbackAdapter : ImageViewerAdapterListener
//        , androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback()
{
    override fun onInit(viewHolder: RecyclerView.ViewHolder) {}
    override fun onDrag(viewHolder: RecyclerView.ViewHolder, view: View, fraction: Float) {}
    override fun onRestore(viewHolder: RecyclerView.ViewHolder, view: View, fraction: Float) {}
    override fun onRelease(viewHolder: RecyclerView.ViewHolder, view: View) {}
    open fun onPageScrollStateChanged(state: Int) {}
    open fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
    open fun onPageSelected(position: Int) {}
}