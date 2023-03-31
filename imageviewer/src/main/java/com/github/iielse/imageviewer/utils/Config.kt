package com.github.iielse.imageviewer.utils

import android.graphics.Color
import androidx.viewpager2.widget.ViewPager2
import com.github.iielse.imageviewer.widgets.video.ExoVideoView

object Config {
    var DEBUG: Boolean = true
    var OFFSCREEN_PAGE_LIMIT: Int = 1
    var VIEWER_ORIENTATION: Int = ViewPager2.ORIENTATION_HORIZONTAL
    var VIEWER_BACKGROUND_COLOR: Int = Color.BLACK
    var DURATION_TRANSITION: Long = 250L
    var DURATION_BG: Long = 150L
    var SWIPE_DISMISS: Boolean = true
    var SWIPE_TOUCH_SLOP = 4f
    var DISMISS_FRACTION: Float = 0.12f
    var TRANSITION_OFFSET_Y = 0
    var TRANSITION_OFFSET_X = 0
    var VIEWER_FIRST_PAGE_SELECTED_DELAY = 300L
    var VIDEO_SCALE_TYPE = ExoVideoView.SCALE_TYPE_FIT_CENTER
}