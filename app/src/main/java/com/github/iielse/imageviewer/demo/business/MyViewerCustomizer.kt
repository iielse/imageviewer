package com.github.iielse.imageviewer.demo.business

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
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
import com.github.iielse.imageviewer.demo.core.ObserverAdapter
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
class MyViewerCustomizer : LifecycleObserver, VHCustomizer, OverlayCustomizer, ViewerCallback {
    private var activity: FragmentActivity? = null
    private var viewerActions: ImageViewerActionViewModel? = null
    private var videoTask: Disposable? = null
    private var lastVideoVH: RecyclerView.ViewHolder? = null
    private var indicatorDecor: View? = null
    private var indicator: TextView? = null
    private var pre: TextView? = null
    private var next: TextView? = null
    private var currentPosition = -1

    /**
     * 对viewer进行自定义封装. 添加自定义指示器.video绑定.图片说明等自定义配置
     */
    fun process(activity: FragmentActivity, builder: ImageViewerBuilder) {
        this.activity = activity
        viewerActions = ViewModelProvider(activity).get(ImageViewerActionViewModel::class.java)
        activity.lifecycle.addObserver(this)
        builder.setVHCustomizer(this)
        builder.setOverlayCustomizer(this)
        builder.setViewerCallback(this)
    }

    override fun initialize(type: Int, viewHolder: RecyclerView.ViewHolder) {
        (viewHolder.itemView as? ViewGroup?)?.let {
            it.addView(it.inflate(R.layout.item_photo_custom_layout))
        }
        when (type) {
            ItemType.SUBSAMPLING -> {
                viewHolder.find<View>(R.id.subsamplingView)?.setOnClickCallback { viewerActions?.dismiss() }
            }
            ItemType.PHOTO -> {
                viewHolder.find<View>(R.id.photoView)?.setOnClickCallback { viewerActions?.dismiss() }
            }
            ItemType.VIDEO -> {
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
        viewHolder.itemView.findViewById<View>(R.id.remove).setOnClickListener {
            (viewHolder.itemView.tag as? MyData?)?.let {
                viewerActions?.remove(listOf(it))
            }
        }
    }

    override fun provideView(parent: ViewGroup): View {
        return parent.inflate(R.layout.layout_indicator).also {
            indicatorDecor = it.findViewById(R.id.indicatorDecor)
            indicator = it.findViewById(R.id.indicator)
            pre = it.findViewById(R.id.pre)
            next = it.findViewById(R.id.next)
            pre?.setOnClickCallback { viewerActions?.setCurrentItem(currentPosition - 1) }
            next?.setOnClickCallback { viewerActions?.setCurrentItem(currentPosition + 1) }
            it.findViewById<View>(R.id.dismiss).setOnClickCallback { viewerActions?.dismiss() }
        }
    }

    override fun onRelease(viewHolder: RecyclerView.ViewHolder, view: View) {
        log("onRelease")
        viewHolder.find<View>(R.id.customizeDecor)
                ?.animate()?.setDuration(200)?.alpha(0f)?.start()
        indicatorDecor?.animate()?.setDuration(200)?.alpha(0f)?.start()
        release()
    }

    override fun onPageSelected(position: Int, viewHolder: RecyclerView.ViewHolder) {
        log("onPageSelected $position")
        currentPosition = position
        indicator?.text = position.toString()
        processSelectVideo(viewHolder)
    }

    private fun processSelectVideo(viewHolder: RecyclerView.ViewHolder) {
        videoTask?.dispose()
        lastVideoVH?.find<ExoVideoView>(R.id.videoView)?.reset()

        when (viewHolder) {
            is VideoViewHolder -> {
                val videoView = viewHolder.find<ExoVideoView>(R.id.videoView)

                val task = object : ObserverAdapter<Long>(videoView?.lifecycleOwner?.lifecycle) {
                    override fun onNext(t: Long) {
                        if (ViewerHelper.simplePlayVideo) {
                            videoView?.resume()
                        } else {
                            val playerControlView = viewHolder.find<PlayerControlView>(R.id.playerControlView)
                            playerControlView?.player = videoView?.player()
                        }
                    }
                }
                Observable.timer(Config.DURATION_TRANSITION + 50, TimeUnit.MILLISECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(task)
                videoTask = task
                lastVideoVH = viewHolder
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    private fun onResume() {
        lastVideoVH?.find<ExoVideoView>(R.id.videoView)?.resume()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    private fun onPause() {
        lastVideoVH?.find<ExoVideoView>(R.id.videoView)?.pause()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private fun onDestroy() {
        lastVideoVH?.find<ExoVideoView>(R.id.videoView)?.release()
        videoTask?.dispose()
        videoTask = null
    }

    private fun release() {
        activity?.lifecycle?.removeObserver(this)
        activity = null
        videoTask?.dispose()
        videoTask = null
        lastVideoVH = null
        indicatorDecor = null
        indicator = null
        pre = null
        next = null
    }
}


fun <T : View> RecyclerView.ViewHolder.find(@IdRes id: Int): T? = itemView.findViewById(id)