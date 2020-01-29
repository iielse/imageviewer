package com.github.iielse.imageviewer.utils

import android.graphics.Color
import androidx.viewpager2.widget.ViewPager2

object Config {
    var DEBUG: Boolean = false
    var VIEWER_ORIENTATION: Int = ViewPager2.ORIENTATION_HORIZONTAL
    var SWIPE_DISMISS: Boolean = true
    var VIEWER_BACKGROUND_COLOR: Int = Color.BLACK
    var OFFSCREEN_PAGE_LIMIT: Int = 1
    var DURATION_TRANSFORMER: Long = 300L
    var DURATION_BG: Long = 200L
    var DURATION_ENTER_START_DELAY = 0L
    var DISMISS_FRACTION: Float = 0.15f
}