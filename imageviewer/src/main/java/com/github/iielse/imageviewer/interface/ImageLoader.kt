package com.github.iielse.imageviewer.`interface`

import android.widget.ImageView
import com.github.iielse.imageviewer.Photo

interface ImageLoader {
    fun load(view : ImageView, photo : Photo )
}