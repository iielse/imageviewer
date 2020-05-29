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
import com.github.iielse.imageviewer.utils.inflate
import com.github.iielse.imageviewer.utils.onDestroy
import com.github.iielse.imageviewer.widgets.PhotoView2
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer

/**
 * viewer 自定义业务&UI
 */
class MyViewerEx(activity: FragmentActivity) {
    private val viewerActions by lazy { ViewModelProvider(activity).get(ImageViewerActionViewModel::class.java) }
    private var indicatorDecor: View? = null
    private var indicator: TextView? = null
    private var pre: TextView? = null
    private var next: TextView? = null
    private var currentPosition = -1
    private var playingVH: RecyclerView.ViewHolder? = null

    init {
        activity.onDestroy { releaseVideo() }
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

                bindVideo(type, myData, viewHolder)
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
                releaseVideo()
            }

            override fun onPageSelected(position: Int) {
                currentPosition = position
                indicator?.text = position.toString()
                releaseVideo()
            }
        })
    }

    private fun releaseVideo() {
        val it = playingVH?.itemView ?: return
        val photoView = it.findViewById<View>(R.id.photoView)
        val videoView = it.findViewById<StandardGSYVideoPlayer>(R.id.videoView)
        val play = it.findViewById<View>(R.id.play)
        play.visibility = View.VISIBLE
        photoView.visibility = View.VISIBLE
        videoView.visibility = View.GONE
        videoView.release()
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
        }
    }

    /**
     *  绑定video类型的事件
     */
    private fun bindVideo(type: Int, data: MyData, viewHolder: RecyclerView.ViewHolder) {
        when (type) {
            ItemType.PHOTO -> {
                val photoView = viewHolder.itemView.findViewById<PhotoView2>(R.id.photoView)
                val videoView = viewHolder.itemView.findViewById<StandardGSYVideoPlayer>(R.id.videoView)
                val play = viewHolder.itemView.findViewById<View>(R.id.play)
                if (data.url.endsWith(".mp4")) {
                    photoView.visibility = View.VISIBLE
                    photoView.isEnabled = false
                    videoView.visibility = View.GONE
                    play.visibility = View.VISIBLE

                    videoView.setUp(data.url, true, "")
                    videoView.titleTextView.visibility = View.GONE
                    videoView.backButton.visibility = View.GONE
                    videoView.setIsTouchWiget(true)
                    videoView.setVideoAllCallBack(object : GSYSampleCallBack() {
                        override fun onAutoComplete(url: String?, vararg objects: Any?) {
                            play.visibility = View.VISIBLE
                            photoView.visibility = View.VISIBLE
                            videoView.visibility = View.GONE
                        }
                    })

                    play.setOnClickCallback {
                        videoView.startPlayLogic()
                        playingVH = viewHolder
                        videoView.visibility = View.VISIBLE
                        play.visibility = View.GONE
                    }
                } else {
                    photoView.visibility = View.VISIBLE
                    photoView.isEnabled = true
                    videoView.visibility = View.GONE
                    play.visibility = View.GONE
                }
            }
        }
    }
}