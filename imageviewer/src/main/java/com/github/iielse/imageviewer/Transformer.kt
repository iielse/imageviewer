package com.github.iielse.imageviewer

import android.widget.ImageView

interface Transformer {
    fun getView(pos: Int): ImageView?
}

class DefaultTransformer : Transformer {
    override fun getView(pos: Int): ImageView? = null
}

