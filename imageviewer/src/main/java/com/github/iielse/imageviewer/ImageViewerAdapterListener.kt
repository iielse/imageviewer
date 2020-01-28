package com.github.iielse.imageviewer

import android.view.View
import androidx.recyclerview.widget.RecyclerView

interface ImageViewerAdapterListener {
    fun onInit(viewHolder: RecyclerView.ViewHolder)
    fun onDrag(viewHolder: RecyclerView.ViewHolder, view: View, fraction: Float)
    fun onRestore(viewHolder: RecyclerView.ViewHolder, view: View, fraction: Float)
    fun onRelease(viewHolder: RecyclerView.ViewHolder, view: View)
}