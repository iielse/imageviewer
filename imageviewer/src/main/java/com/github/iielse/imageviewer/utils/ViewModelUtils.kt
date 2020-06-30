package com.github.iielse.imageviewer.utils

import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.iielse.imageviewer.ImageViewerDialogFragment

internal object ViewModelUtils {
    fun <T : ViewModel> provideViewModel(view: View, modelClass: Class<T>): T? {
        return (view.activity as? FragmentActivity?)?.supportFragmentManager?.fragments?.find { it is ImageViewerDialogFragment }
                ?.let { ViewModelProvider(it).get(modelClass) }
    }
}