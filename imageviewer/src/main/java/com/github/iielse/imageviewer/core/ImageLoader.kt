package com.github.iielse.imageviewer.core

import android.widget.ImageView

interface ImageLoader {
    fun load(view : ImageView, photo : Photo)
}