package com.github.iielse.imageviewer.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.FrameLayout
import com.github.iielse.imageviewer.utils.TransitionEndHelper
import com.github.iielse.imageviewer.utils.TransitionStartHelper

class InterceptLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : FrameLayout(context, attrs, defStyleAttr) {
    override fun onInterceptTouchEvent(ev: MotionEvent?) = TransitionStartHelper.animating || TransitionEndHelper.animating
}