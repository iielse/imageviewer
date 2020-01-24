package com.github.iielse.imageviewer

import androidx.lifecycle.ViewModel

class ImageViewerViewModel : ViewModel() {
    var dataProvider: DataProvider? = null
    var transform: Transform? = null

    fun initialize(dataProvider: DataProvider?, transform: Transform?) {
        this.dataProvider = dataProvider
        this.transform = transform
    }

    fun getInitial(): List<Photo> {
        return dataProvider?.getInitial() ?: listOf()
    }

//    fun getTransformRect(pos: Int): Rect {
//        return transform?.getOriginView(pos)?.let {
//            Rect().apply { it.getHitRect(this) }
//        } ?: RECT_NULL
//    }
}