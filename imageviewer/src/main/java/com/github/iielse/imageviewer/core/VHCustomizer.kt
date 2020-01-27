package com.github.iielse.imageviewer.core

import androidx.recyclerview.widget.RecyclerView

interface VHCustomizer {
    fun initialize(type: Int, viewHolder: RecyclerView.ViewHolder) {}
    fun bind(type: Int, data: Photo, viewHolder: RecyclerView.ViewHolder) {}
}
