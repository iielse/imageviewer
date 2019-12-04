package com.github.iielse.imageviewer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.github.iielse.imageviewer.photoview.PhotoView2
import com.github.iielse.imageviewer.util.ViewerUtil.colorEvaluator
import kotlinx.android.synthetic.main.fragment_image_viewer_dialog.*

class ImageViewerDialogFragment : BaseDialogFragment() {

    private val viewModel by lazy { ViewModelProviders.of(this).get(ImageViewerViewModel::class.java) }
    private val adapter by lazy { ImageViewerAdapter() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_image_viewer_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (viewer.getChildAt(0) as? RecyclerView?)?.clipChildren = false
        viewer.adapter = adapter
        adapter.setListener(::handleAdapterListener)

        viewModel.photos.observe(this, Observer {
            adapter.submitList(it)
        })

        viewModel.test()
    }

    private fun handleAdapterListener(action: String, item: Any?) {
        when (action) {
            ITEM_DRAG -> {
                val fraction = (item as Pair<*, *>).first as Float
                val act = (item as Pair<*, *>).second as Int
                container.setBackgroundColor(colorEvaluator.evaluate(fraction, 0xff000000.toInt(), 0x00000000.toInt()))
                if (act == PhotoView2.ACTION_RELEASE) {

                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter.setListener(null)
    }
}
