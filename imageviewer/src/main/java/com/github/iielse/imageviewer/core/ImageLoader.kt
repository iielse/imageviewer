package com.github.iielse.imageviewer.core

import android.widget.ImageView
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView

interface ImageLoader<T : Photo> {
    fun load(view : ImageView, data : T)
    fun load(subsamplingView : SubsamplingScaleImageView, data: T)
}