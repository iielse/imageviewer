package com.github.iielse.imageviewer.adapter

import androidx.annotation.IntDef

object ItemType {
    const val UNKNOWN = -1
    const val PHOTO = 1
    const val SUBSAMPLING = 2
    const val VIDEO = 3

    @Target(AnnotationTarget.TYPE)
    @IntDef(PHOTO, SUBSAMPLING, VIDEO)
    @Retention(AnnotationRetention.SOURCE)
    annotation class Type
}