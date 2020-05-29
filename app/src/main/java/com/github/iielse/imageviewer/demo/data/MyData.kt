package com.github.iielse.imageviewer.demo.data

import com.github.iielse.imageviewer.core.Photo

data class MyData(val id: Long,
                  val url: String,
                  val subsampling: Boolean = false,
                  val desc: String = "[$id] Caption or other information for this picture [$id]") : Photo {
    override fun id(): Long = id
    override fun subsampling() = subsampling
}
