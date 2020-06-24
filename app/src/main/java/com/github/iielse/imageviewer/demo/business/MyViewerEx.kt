package com.github.iielse.imageviewer.demo.business

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
import com.github.iielse.imageviewer.demo.R
import com.github.iielse.imageviewer.demo.data.MyData
import com.github.iielse.imageviewer.demo.utils.setOnClickCallback
import com.github.iielse.imageviewer.utils.*
import com.github.iielse.imageviewer.viewholders.VideoViewHolder
import com.github.iielse.imageviewer.widgets.video.ExoVideoView

/**
 * viewer 自定义业务&UI
 */
class MyViewerEx(activity: FragmentActivity) {
    private val viewerActions by lazy { ViewModelProvider(activity).get(ImageViewerActionViewModel::class.java) }
    private var lastVideoViewHolder: RecyclerView.ViewHolder? = null
    private var indicatorDecor: View? = null
    private var indicator: TextView? = null
    private var pre: TextView? = null
    private var next: TextView? = null
    private var currentPosition = -1

    init {
        activity.onResume { lastVideoViewHolder?.itemView?.findViewById<ExoVideoView>(R.id.videoView)?.resume() }
        activity.onPause { lastVideoViewHolder?.itemView?.findViewById<ExoVideoView>(R.id.videoView)?.pause() }
        activity.onDestroy {  lastVideoViewHolder?.itemView?.findViewById<ExoVideoView>(R.id.videoView)?.release() }
    }

    /**
     * 对viewer进行自定义封装. 添加自定义指示器.video绑定.图片说明等自定义配置
     */
    fun attach(builder: ImageViewerBuilder) {
        builder.setVHCustomizer(object : VHCustomizer {
            override fun initialize(type: Int, viewHolder: RecyclerView.ViewHolder) {
                (viewHolder.itemView as? ViewGroup?)?.let {
                    it.addView(it.inflate(R.layout.item_photo_custom_layout))
                }
                initialClickDismissEvent(type, viewHolder)
            }

            override fun bind(type: Int, data: Photo, viewHolder: RecyclerView.ViewHolder) {
                val myData = data as MyData
                viewHolder.itemView.findViewById<TextView>(R.id.exText).text = myData.desc
            }
        })
        builder.setOverlayCustomizer(object : OverlayCustomizer {
            override fun provideView(parent: ViewGroup): View? {
                return parent.inflate(R.layout.layout_indicator).also {
                    indicatorDecor = it.findViewById(R.id.indicatorDecor)
                    indicator = it.findViewById(R.id.indicator)
                    pre = it.findViewById(R.id.pre)
                    next = it.findViewById(R.id.next)

                    pre?.setOnClickCallback { viewerActions.setCurrentItem(currentPosition - 1) }
                    next?.setOnClickCallback { viewerActions.setCurrentItem(currentPosition + 1) }
                    it.findViewById<View>(R.id.dismiss).setOnClickCallback { viewerActions.dismiss() }
                }
            }
        })
        builder.setViewerCallback(object : ViewerCallback {
            override fun onRelease(viewHolder: RecyclerView.ViewHolder, view: View) {
                viewHolder.itemView.findViewById<View>(R.id.customizeDecor)
                        .animate().setDuration(200).alpha(0f).start()
                indicatorDecor?.animate()?.setDuration(200)?.alpha(0f)?.start()
            }

            override fun onPageSelected(position: Int, viewHolder: RecyclerView.ViewHolder) {
                currentPosition = position
                indicator?.text = position.toString()

                autoPlayVideo(position, viewHolder)
            }
        })
    }

    /**
     *  给大图添加点击关闭事件
     */
    private fun initialClickDismissEvent(type: Int, viewHolder: RecyclerView.ViewHolder) {
        when (type) {
            ItemType.SUBSAMPLING -> {
                val imageView = viewHolder.itemView.findViewById<View>(R.id.subsamplingView)
                imageView.setOnClickCallback {
                    viewerActions.dismiss()
                }
            }
            ItemType.PHOTO -> {
                val imageView = viewHolder.itemView.findViewById<View>(R.id.photoView)
                imageView.setOnClickCallback {
                    viewerActions.dismiss()
                }
            }
            ItemType.VIDEO -> {
                val view = viewHolder.itemView.findViewById<View>(R.id.videoView)
                view.setOnClickCallback {
                    viewerActions.dismiss()
                }
            }
        }
    }

    private fun autoPlayVideo(pos: Int, viewHolder: RecyclerView.ViewHolder) {
        lastVideoViewHolder?.itemView?.findViewById<ExoVideoView>(R.id.videoView)?.reset()
        when (viewHolder) {
            is VideoViewHolder -> {
                viewHolder.itemView.findViewById<ExoVideoView>(R.id.videoView)?.resume()
                lastVideoViewHolder = viewHolder
            }
        }
    }
}