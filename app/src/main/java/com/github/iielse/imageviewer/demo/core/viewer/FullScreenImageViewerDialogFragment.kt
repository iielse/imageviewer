package com.github.iielse.imageviewer.demo.core.viewer

import android.view.View
import android.view.Window
import android.view.WindowManager
import com.github.iielse.imageviewer.ImageViewerDialogFragment

/**
 * 自定义ImageViewerDialogFragment
 * 此类主要对于 window 进行个性化再定制
 */
class FullScreenImageViewerDialogFragment : ImageViewerDialogFragment() {
    override fun setWindow(win: Window) {
        super.setWindow(win)
        win.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        win.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_FULLSCREEN
    }
}