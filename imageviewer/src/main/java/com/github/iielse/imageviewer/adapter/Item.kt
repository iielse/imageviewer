package com.github.iielse.imageviewer.adapter

import com.github.iielse.imageviewer.core.Photo

data class Item(
        val type: Int,
        val id: Long,
        val extra: Any? = null
) {
    inline fun <reified T> extra(): T? {
        return extra as? T?
    }

    companion object {
        fun from(data: Photo): Item {
            return Item(
                    type = if (data.subsampling()) ItemType.SUBSAMPLING else ItemType.PHOTO,
                    id = data.id(),
                    extra = data
            )
        }
    }
}