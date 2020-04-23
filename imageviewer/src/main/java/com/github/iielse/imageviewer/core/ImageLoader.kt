package com.github.iielse.imageviewer.core

import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView

interface ImageLoader {
    fun load(view: ImageView, data: Photo, viewHolder: RecyclerView.ViewHolder) {}
    fun load(subsamplingView: SubsamplingScaleImageView, data: Photo, viewHolder: RecyclerView.ViewHolder) {}
}