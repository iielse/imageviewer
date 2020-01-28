package com.github.iielse.imageviewer.utils

import androidx.viewpager2.widget.ViewPager2

object Config {
    var DEBUG = true
    var VIEWER_ORIENTATION = ViewPager2.ORIENTATION_HORIZONTAL
    var SWIPE_DISMISS = true
    var OFFSCREEN_PAGE_LIMIT = 1
    var DURATION_TRANSFORMER = 300L
    var DURATION_BG = 200L
    var DISMISS_FRACTION = 0.15f
}