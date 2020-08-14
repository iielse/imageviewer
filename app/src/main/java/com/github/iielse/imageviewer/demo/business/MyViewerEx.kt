package com.github.iielse.imageviewer.demo.business

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.IdRes
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
import com.github.iielse.imageviewer.demo.utils.*
import com.github.iielse.imageviewer.utils.Config
import com.github.iielse.imageviewer.viewholders.VideoViewHolder
import com.github.iielse.imageviewer.widgets.video.ExoVideoView
import com.google.android.exoplayer2.ui.PlayerControlView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

/**
 * viewer 自定义业务&UI
 */
class MyViewerEx(activity: FragmentActivity) {
    private val viewerActions by lazy { ViewModelProvider(activity).get(ImageViewerActionViewModel::class.java) }
    private var videoTask: Disposable? = null
    private var lastVideoViewHolder: RecyclerView.ViewHolder? = null
    private var indicatorDecor: View? = null
    private var indicator: TextView? = null
    private var pre: TextView? = null
    private var next: TextView? = null
    private var currentPosition = -1

    init {
        activity.onResume { lastVideoViewHolder?.find<ExoVideoView>(R.id.videoView)?.resume() }
        activity.onPause { lastVideoViewHolder?.find<ExoVideoView>(R.id.videoView)?.pause() }
        activity.onDestroy {
            lastVideoViewHolder?.find<ExoVideoView>(R.id.videoView)?.release()
            videoTask?.dispose()
        }
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

                when (viewHolder) {
                    is VideoViewHolder -> {
                        (viewHolder.itemView as? ViewGroup?)?.let {
                            it.addView(it.inflate(R.layout.item_video_custom_layout))
                        }
                        val playerControlView = viewHolder.find<PlayerControlView>(R.id.playerControlView)
                        playerControlView?.visibility = if (ViewerHelper.simplePlayVideo) View.GONE else View.VISIBLE

                        viewHolder.find<ExoVideoView>(R.id.videoView)?.let {
                            it.setOnClickCallback { toast("video clicked") }
                            it.setOnLongClickListener {
                                toast("video long clicked")
                                true
                            }
                        }
                    }
                }
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
                viewHolder.find<View>(R.id.customizeDecor)
                        ?.animate()?.setDuration(200)?.alpha(0f)?.start()
                indicatorDecor?.animate()?.setDuration(200)?.alpha(0f)?.start()
            }

            override fun onPageSelected(position: Int, viewHolder: RecyclerView.ViewHolder) {
                log("onPageSelected $position")
                currentPosition = position
                indicator?.text = position.toString()

                processSelectVideo(position, viewHolder)
            }
        })
    }

    /**
     *  给大图添加点击关闭事件
     */
    private fun initialClickDismissEvent(type: Int, viewHolder: RecyclerView.ViewHolder) {
        when (type) {
            ItemType.SUBSAMPLING -> viewHolder.find<View>(R.id.subsamplingView)?.setOnClickCallback { viewerActions.dismiss() }
            ItemType.PHOTO -> viewHolder.find<View>(R.id.photoView)?.setOnClickCallback { viewerActions.dismiss() }
        }
    }

    private fun processSelectVideo(pos: Int, viewHolder: RecyclerView.ViewHolder) {
        videoTask?.dispose()
        lastVideoViewHolder?.find<ExoVideoView>(R.id.videoView)?.reset()

        when (viewHolder) {
            is VideoViewHolder -> {
                val videoView = viewHolder.find<ExoVideoView>(R.id.videoView)

                videoTask = Observable.timer(Config.DURATION_TRANSITION + 50, TimeUnit.MILLISECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .doOnNext {
                            if (ViewerHelper.simplePlayVideo) {
                                videoView?.resume()
                            } else {
                                val playerControlView = viewHolder.find<PlayerControlView>(R.id.playerControlView)
                                playerControlView?.player = videoView?.player()
                            }
                        }
                        .subscribe().bindLifecycle(videoView)
                lastVideoViewHolder = viewHolder
            }
        }
    }
}


fun <T : View> RecyclerView.ViewHolder.find(@IdRes id: Int): T? = itemView.findViewById(id)