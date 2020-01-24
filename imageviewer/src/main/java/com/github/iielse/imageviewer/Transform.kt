package com.github.iielse.imageviewer

import android.widget.ImageView

interface Transform {
    fun getOriginView(pos: Int): ImageView?
}