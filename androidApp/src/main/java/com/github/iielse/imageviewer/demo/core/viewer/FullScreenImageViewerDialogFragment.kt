package com.github.iielse.imageviewer.demo.core.viewer

import android.view.Window
import androidx.core.view.WindowInsetsControllerCompat
import com.github.iielse.imageviewer.ImageViewerDialogFragment

/**
 * 自定义ImageViewerDialogFragment
 * 此类主要对于 window 进行个性化再定制
 */
class FullScreenImageViewerDialogFragment : ImageViewerDialogFragment() {
    override fun setWindow(win: Window) {
        super.setWindow(win)
        WindowInsetsControllerCompat(win, win.decorView).apply {
            isAppearanceLightStatusBars = false
            isAppearanceLightNavigationBars = false
        }
    }
}
