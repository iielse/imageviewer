package com.github.iielse.imageviewer.utils

import android.graphics.Color
import androidx.viewpager2.widget.ViewPager2

object Config {
    var DEBUG: Boolean = true
    var VIEWER_ORIENTATION: Int = ViewPager2.ORIENTATION_HORIZONTAL
    var SWIPE_DISMISS: Boolean = true
    var VIEWER_BACKGROUND_COLOR: Int = Color.BLACK
    var OFFSCREEN_PAGE_LIMIT: Int = 1
    var DURATION_TRANSFORMER: Long = if (DEBUG) 3000L else 300L
    var DURATION_BG: Long = if (DEBUG) 2000L else 200L
    var DURATION_ENTER_START_DELAY = if (DEBUG) 800L else 80L
    var DISMISS_FRACTION: Float = 0.15f
}