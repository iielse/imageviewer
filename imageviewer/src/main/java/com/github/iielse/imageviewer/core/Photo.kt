package com.github.iielse.imageviewer.core

interface Photo {
    fun id(): Long
    fun subsampling(): Boolean = false
    fun url(): String
}
