package com.github.iielse.imageviewer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.layout_imageviewer.*

class ImageViewerDialogFragment : DialogFragment() {
    private val viewModel by lazy { ViewModelProviders.of(this).get(ImageViewerViewModel::class.java) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.layout_imageviewer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.photos.observe(this, Observer {
            viewer.set(it)
        })

    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}