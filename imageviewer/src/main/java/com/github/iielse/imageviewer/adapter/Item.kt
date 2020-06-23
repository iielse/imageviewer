package com.github.iielse.imageviewer.adapter

import com.github.iielse.imageviewer.core.Photo

data class Item(
        val type: @ItemType.Type Int,
        val id: Long,
        val extra: Any? = null
) {
    inline fun <reified T> extra(): T? {
        return extra as? T?
    }

    companion object {
        fun from(data: Photo): Item {
            return Item(
                    type = data.itemType(),
                    id = data.id(),
                    extra = data
            )
        }
    }
}