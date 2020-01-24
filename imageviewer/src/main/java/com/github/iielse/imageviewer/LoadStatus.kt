package com.github.iielse.imageviewer

enum class LoadStatus {
    IDLE,
    LOADING,
    SUCCESS,
    FAILURE, ;

    val isLoading get() = this == LOADING
}

fun LoadStatus?.isRefreshing(): Boolean = this?.isLoading ?: false