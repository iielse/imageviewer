package com.github.iielse.imageviewer.demo.viewer

import com.github.iielse.imageviewer.core.DataProvider
import com.github.iielse.imageviewer.core.Photo
import com.github.iielse.imageviewer.demo.data.myData

class MyDataLoadAllAtOnceProvider() : DataProvider {
    override fun loadInitial(): List<Photo> {
        return myData
    }
}
