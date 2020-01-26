package com.github.iielse.imageviewer.core

import android.widget.ImageView

interface Transformer {
    fun getView(key: Int): ImageView?
}

class DefaultTransformer : Transformer {
    override fun getView(key: Int): ImageView? = null
}

