package com.github.iielse.imageviewer.demo

import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.github.iielse.imageviewer.ImageViewerActionViewModel
import com.github.iielse.imageviewer.ImageViewerBuilder
import com.github.iielse.imageviewer.adapter.ItemType
import com.github.iielse.imageviewer.core.OverlayCustomizer
import com.github.iielse.imageviewer.core.Photo
import com.github.iielse.imageviewer.core.VHCustomizer
import com.github.iielse.imageviewer.core.ViewerCallback
import com.github.iielse.imageviewer.utils.inflate

class MyCustomController(private val activity: FragmentActivity) {
    private val viewerActions by lazy { ViewModelProvider(activity).get(ImageViewerActionViewModel::class.java) }

    private var indicatorDecor: View? = null
    private var overlayIndicator: TextView? = null
    private var preIndicator: TextView? = null
    private var nextIndicator: TextView? = null
    private var currentPosition = -1

    fun init(builder: ImageViewerBuilder) {
        builder.setVHCustomizer(object : VHCustomizer {
            override fun initialize(type: Int, viewHolder: RecyclerView.ViewHolder) {
                (viewHolder.itemView as? ViewGroup?)?.let {
                    it.addView(it.inflate(R.layout.item_photo_custom_layout))

                    when (type){
                        ItemType.SUBSAMPLING -> {
                            val imageView = it.findViewById<View>(R.id.subsamplingView)
                            imageView.setOnClickListener {
                                viewerActions.dismiss()
                            }
                        }
                        ItemType.PHOTO -> {
                            val imageView = it.findViewById<View>(R.id.photoView)
                            imageView.setOnClickListener {
                                viewerActions.dismiss()
                            }
                        }
                    }
                }
            }

            override fun bind(type: Int, data: Photo, viewHolder: RecyclerView.ViewHolder) {
                (viewHolder.itemView as? ViewGroup?)?.let {
                    val x = data as MyData
                    it.findViewById<TextView>(R.id.exText).text = x.desc
                }
            }
        })
        builder.setOverlayCustomizer(object : OverlayCustomizer {
            override fun provideView(parent: ViewGroup): View? {
                return parent.inflate(R.layout.layout_indicator).also {
                    indicatorDecor = it.findViewById(R.id.indicatorDecor)
                    overlayIndicator = it.findViewById(R.id.indicator)
                    preIndicator = it.findViewById(R.id.pre)
                    nextIndicator = it.findViewById(R.id.next)

                    preIndicator?.setOnClickListener {
                        viewerActions.setCurrentItem(currentPosition - 1)
                    }
                    nextIndicator?.setOnClickListener {
                        viewerActions.setCurrentItem(currentPosition + 1)
                    }

                    it.findViewById<View>(R.id.dismiss).setOnClickListener {
                        viewerActions.dismiss()
                    }
                }
            }
        })
        builder.setViewerCallback(object : ViewerCallback {
            override fun onRelease(viewHolder: RecyclerView.ViewHolder, view: View) {
                viewHolder.itemView.findViewById<View>(R.id.customizeDecor)
                        .animate().setDuration(200).alpha(0f).start()

                indicatorDecor?.animate()?.setDuration(200)?.alpha(0f)?.start()
            }

            override fun onPageSelected(position: Int) {
                currentPosition = position
                overlayIndicator?.text = position.toString()
            }
        })
    }
}