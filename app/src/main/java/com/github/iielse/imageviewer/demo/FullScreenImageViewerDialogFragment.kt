package com.github.iielse.imageviewer.demo

import android.graphics.Color
import android.os.Build
import android.view.Window
import android.view.WindowManager
import com.github.iielse.imageviewer.ImageViewerDialogFragment


class FullScreenImageViewerDialogFragment : ImageViewerDialogFragment() {
    override fun setWindow(win: Window) {
        super.setWindow(win)
        win.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS or WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity?.window?.statusBarColor = Color.BLACK
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity?.window?.statusBarColor = context?.resources?.getColor(R.color.colorPrimaryDark)
                    ?: Color.TRANSPARENT
        }
    }
}