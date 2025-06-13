package com.github.iielse.imageviewer.demo.core.viewer

import android.app.Dialog
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import com.github.iielse.imageviewer.ImageViewerDialogFragment


/**
 * 自定义ImageViewerDialogFragment
 * 此类主要对于 window 进行个性化再定制
 */
class FullScreenImageViewerDialogFragment : ImageViewerDialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(requireActivity(), com.github.iielse.imageviewer.demo.R.style.FullScreenDialog).apply {
            setCanceledOnTouchOutside(true)
            window?.let(::setWindow)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState).also {
            // 重要：设置为false，避免系统自动添加padding
            it?.fitsSystemWindows = false
        }
    }


    override fun setWindow(win: Window) {
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(win.attributes)
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT

        // 设置系统UI的可见性，让内容延伸到状态栏和导航栏后面
        win.attributes = layoutParams

        // 设置系统UI的Flag，使内容可以延伸到状态栏和导航栏区域
        win.decorView.setSystemUiVisibility(
            (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION)
        )

        // 设置状态栏和导航栏透明
        win.statusBarColor = Color.TRANSPARENT
        win.navigationBarColor = Color.TRANSPARENT

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            win.attributes.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }
    }
}