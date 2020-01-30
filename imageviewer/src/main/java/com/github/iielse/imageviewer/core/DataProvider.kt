package com.github.iielse.imageviewer.core

interface DataProvider {
    fun loadInitial(): List<Photo>
    fun loadAfter(key: Long, callback: (List<Photo>) -> Unit)
    fun loadBefore(key: Long, callback: (List<Photo>) -> Unit)
}