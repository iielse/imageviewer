package com.github.iielse.imageviewer.core

import android.view.View
import android.view.ViewGroup

interface OverlayCustomizer {
    fun provideView(parent: ViewGroup): View? = null
}