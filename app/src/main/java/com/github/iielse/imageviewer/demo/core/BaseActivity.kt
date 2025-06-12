package com.github.iielse.imageviewer.demo.core

import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.github.iielse.imageviewer.demo.utils.colorInt
import com.github.iielse.imageviewer.demo.utils.useStatusBar
import androidx.core.graphics.drawable.toDrawable

open class BaseActivity : FragmentActivity() {
    open val navigationBarTranslucent = false
    open val windowBackgroundColorLight = "#FFFFFF".colorInt
    open val statusBarColorLight = "#FFFFFF".colorInt
    open val navigationBarColorLight = "#FFFFFF".colorInt

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        useStatusBar(
            statusBarLight = true,
            navigationBarLight = true,
            statusBarTranslucent = true,
            navigationBarTranslucent = navigationBarTranslucent
        )
        window.statusBarColor = statusBarColorLight
        window.navigationBarColor = navigationBarColorLight
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.navigationBarDividerColor = navigationBarColorLight
        }
        window.setBackgroundDrawable(windowBackgroundColorLight.toDrawable())
    }
}