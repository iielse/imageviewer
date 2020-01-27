package com.github.iielse.imageviewer.core

import android.widget.ImageView
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView

interface ImageLoader {
    fun load(view : ImageView, data : Photo)
    fun load(subsamplingView : SubsamplingScaleImageView, data: Photo)
}