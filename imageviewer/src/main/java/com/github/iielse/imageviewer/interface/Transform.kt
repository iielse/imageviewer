package com.github.iielse.imageviewer.`interface`

import android.widget.ImageView

interface Transform {
    fun getOriginView(pos: Int): ImageView?
}