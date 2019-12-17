package com.github.iielse.imageviewer

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.github.iielse.imageviewer.Config.RECT_NULL
import com.github.iielse.imageviewer.core.AdapterCallback
import com.github.iielse.imageviewer.photoview.PhotoView2
import com.github.iielse.imageviewer.util.ViewerUtil
import kotlinx.android.synthetic.main.fragment_image_viewer_dialog.*

class ImageViewerDialogFragment : BaseDialogFragment() {
    private val viewModel by lazy { ViewModelProviders.of(requireActivity()).get(ImageViewerViewModel::class.java) }
    private val adapter by lazy { ImageViewerAdapter() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_image_viewer_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter.setListener(adapterListener)
        adapter.set(viewModel.getInitial())
        (viewer.getChildAt(0) as? RecyclerView?)?.clipChildren = false
        viewer.adapter = adapter

        handleTransformAnimation()
    }


    private val adapterListener by lazy {
        object : AdapterCallback {
            override fun invoke(action: String, item: Any?) {
                when (action) {
                    ITEM_DRAG -> {
                        val fraction = (item as Pair<*, *>).first as Float
                        val act = (item as Pair<*, *>).second as Int
                        container.updateBackgroundColor(fraction)
                        if (act == PhotoView2.ACTION_RELEASE) {
                            container.animateBackgroundColor(Color.BLACK)
                        }
                    }
                    ITEM_INIT -> {
                    }
                }
            }
        }
    }

    private fun handleTransformAnimation(view: PhotoView2) {
        ViewerUtil.transform()
        val rect = viewModel.getTransformRect(0)
        if (rect == RECT_NULL) {
            view.alpha = 0f
            view.animate().alpha(1f).start()
        } else {

        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        container.release()
        adapter.setListener(null)
    }
}
