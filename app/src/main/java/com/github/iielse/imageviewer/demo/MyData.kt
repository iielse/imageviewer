package com.github.iielse.imageviewer.demo

import com.github.iielse.imageviewer.core.Photo

data class MyData(val id: Long, val url: String = "", val desc: String = desc(id)) : Photo {
    override fun id(): Long = id
    override fun subsampling() = id % 5 == 0L
}

private fun desc(num: Long): String {
    return when (num % 5) {
        0L -> "desc 1111 desc 1111 desc 1111 desc 1111 desc 1111 desc 1111 desc 1111 desc 1111 desc 1111 desc 1111 desc 1111 "
        1L -> "desc 2222 desc 2222 desc 2222 desc 2222 desc 2222 desc 2222 desc 2222 desc 2222 desc 2222 desc 2222 desc 2222 "
        2L -> "desc 3333 desc 3333 desc 3333 desc 3333 desc 3333 desc 3333 desc 3333 desc 3333 desc 3333 desc 3333 desc 3333 "
        3L -> "desc 4444 desc 4444 desc 4444 desc 4444 desc 4444 desc 4444 desc 4444 desc 4444 desc 4444 desc 4444 desc 4444 "
        else -> "desc 5555 desc 5555 desc 5555 desc 5555 desc 5555 desc 5555 desc 5555 desc 5555 desc 5555 desc 5555 desc 5555 "
    }
}
