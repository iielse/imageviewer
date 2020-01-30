package com.github.iielse.imageviewer.utils

import android.graphics.Color
import androidx.viewpager2.widget.ViewPager2

object Config {
    var DEBUG: Boolean = true
    var VIEWER_ORIENTATION: Int = ViewPager2.ORIENTATION_HORIZONTAL
    var SWIPE_DISMISS: Boolean = true
    var VIEWER_BACKGROUND_COLOR: Int = Color.BLACK
    var OFFSCREEN_PAGE_LIMIT: Int = 1
    var DURATION_TRANSITION: Long = 400L
    var DURATION_BG: Long = 300L
    var DISMISS_FRACTION: Float = 0.15f
}