package github.iielse.imageviewer.demo

import android.util.Log

data class Picture(
        val url: String,
        val width: Int,
        val height: Int
)

data class Feed(
        val id: Int,
        val title: String,
        val cover: Picture
)

fun fetch(pageNo: Int) {

    if (pageNo == 0) {
    }
}

fun test() {
}


data class Temp(
        val roomImageUrl: String,
        val roomNo: Int,
        val roomOwnerNickName: String
)
