package com.github.iielse.imageviewer

import android.widget.ImageView
import androidx.lifecycle.ViewModel
import com.github.iielse.imageviewer.`interface`.DataProvider
import com.github.iielse.imageviewer.model.Photo

class ImageViewerViewModel : ViewModel() {
    private lateinit var dataProvider: DataProvider

    fun initialize(dataProvider: DataProvider) {
        this.dataProvider = dataProvider
    }

    fun getInitial(): Map<Photo, ImageView?> {
        return dataProvider.getInitial()
    }
}