package com.github.iielse.imageviewer.demo.utils

import java.util.regex.Pattern

object VideoUtils {
    fun isVideoSource(sourceUrl: String): Boolean {
        return Pattern.compile(".+(://).+\\.(mp4|wmv|avi|mpeg|rm|rmvb|flv|3gp|mov|mkv|mod|)")
                .matcher(sourceUrl).matches()
    }
}