package com.github.iielse.imageviewer

import android.graphics.Rect
import androidx.lifecycle.ViewModel
import com.github.iielse.imageviewer.Config.RECT_NULL
import com.github.iielse.imageviewer.`interface`.DataProvider
import com.github.iielse.imageviewer.`interface`.Transform
import com.github.iielse.imageviewer.model.Photo

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