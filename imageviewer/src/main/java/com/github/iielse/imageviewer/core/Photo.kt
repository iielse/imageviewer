package com.github.iielse.imageviewer.core

import com.github.iielse.imageviewer.adapter.ItemType

interface Photo {
    fun id(): Long
    fun itemType(): @ItemType.Type Int
    fun extra(): Any = this
}
