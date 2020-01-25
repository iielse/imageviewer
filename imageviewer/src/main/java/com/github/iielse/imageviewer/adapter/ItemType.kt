package com.github.iielse.imageviewer.adapter



object ItemType {
    var itemTypeProvider = 1
    val UNKNOWN = -1
    val MORE_LOADING = itemTypeProvider++
    val MORE_RETRY = itemTypeProvider++
    val NO_MORE = itemTypeProvider++
    val PHOTO = itemTypeProvider++
    val SUBSAMPLING = itemTypeProvider++
}