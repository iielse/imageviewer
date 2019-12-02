package com.github.iielse.imageviewer

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.Window
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager

open class BaseDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(requireActivity(), R.style.Theme_Light_NoTitle_ViewerDialog).apply {
            setCanceledOnTouchOutside(true)
            window?.let(::setWindow)
        }
    }

    open fun setWindow(win: Window) {
        win.setWindowAnimations(R.style.Animation_Keep)
        win.decorView.setPadding(0, 0, 0, 0)
        val lp = win.attributes
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.MATCH_PARENT
        win.attributes = lp
        win.setGravity(Gravity.CENTER)
    }

    fun show(fragmentManager: FragmentManager?) {
        when {
            fragmentManager == null -> if (Config.DEBUG) Log.e(javaClass.simpleName, "fragmentManager is detach after parent destroy")
            fragmentManager.isStateSaved -> if (Config.DEBUG) Log.e(javaClass.simpleName, "dialog fragment show when fragmentManager isStateSaved")
            else -> show(fragmentManager, javaClass.simpleName)
        }
    }
}