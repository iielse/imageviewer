package com.github.iielse.imageviewer.demo.business

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.github.iielse.imageviewer.ImageViewerActionViewModel
import com.github.iielse.imageviewer.ImageViewerBuilder
import com.github.iielse.imageviewer.core.OverlayCustomizer
import com.github.iielse.imageviewer.core.Photo
import com.github.iielse.imageviewer.core.VHCustomizer
import com.github.iielse.imageviewer.core.ViewerCallback
import com.github.iielse.imageviewer.demo.R
import com.github.iielse.imageviewer.demo.core.ObserverAdapter
import com.github.iielse.imageviewer.demo.data.MyData
import com.github.iielse.imageviewer.demo.data.Service
import com.github.iielse.imageviewer.demo.databinding.ItemVideoCustomLayoutBinding
import com.github.iielse.imageviewer.demo.databinding.LayoutIndicatorBinding
import com.github.iielse.imageviewer.demo.utils.inflate
import com.github.iielse.imageviewer.demo.utils.lifecycleOwner
import com.github.iielse.imageviewer.demo.utils.setOnClickCallback
import com.github.iielse.imageviewer.demo.utils.toast
import com.github.iielse.imageviewer.utils.Config
import com.github.iielse.imageviewer.viewholders.PhotoViewHolder
import com.github.iielse.imageviewer.viewholders.SubsamplingViewHolder
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
class SimpleViewerCustomizer : LifecycleEventObserver, VHCustomizer, OverlayCustomizer, ViewerCallback {
    private var activity: FragmentActivity? = null
    private var testDataViewModel: TestDataViewModel? = null
    private var viewerViewModel: ImageViewerActionViewModel? = null
    private var videoTask: Disposable? = null
    private var lastVideoVH: VideoViewHolder? = null
    private var binding: LayoutIndicatorBinding? = null
    private var currentPosition = -1

    /**
     * 对viewer进行自定义封装. 添加自定义指示器.video绑定.图片说明等自定义配置
     */
    fun process(activity: FragmentActivity, builder: ImageViewerBuilder) {
        this.activity = activity
        testDataViewModel = ViewModelProvider(activity).get(TestDataViewModel::class.java)
        viewerViewModel = ViewModelProvider(activity).get(ImageViewerActionViewModel::class.java)
        activity.lifecycle.addObserver(this)
        builder.setVHCustomizer(this)
        builder.setOverlayCustomizer(this)
        builder.setViewerCallback(this)
    }

    override fun initialize(type: Int, viewHolder: RecyclerView.ViewHolder) {
        (viewHolder.itemView as? ViewGroup?)?.let {
            it.addView(it.inflate(R.layout.item_photo_custom_layout))
        }
        when (viewHolder) {
            is SubsamplingViewHolder -> viewHolder.binding.subsamplingView.setOnClickCallback { viewerViewModel?.dismiss() }
            is PhotoViewHolder -> viewHolder.binding.photoView.setOnClickCallback { viewerViewModel?.dismiss() }
            is VideoViewHolder -> {
                val customBinding = ItemVideoCustomLayoutBinding.inflate(LayoutInflater.from(viewHolder.binding.root.context))
                viewHolder.binding.root.addView(customBinding.root)
                customBinding.playerControlView.visibility = if (ViewerHelper.simplePlayVideo) View.GONE else View.VISIBLE
                viewHolder.binding.videoView.let { videoView ->
                    videoView.setOnClickCallback {
                        toast("video clicked")
                        FullVideoActivity.start(videoView.context, videoView)
                    }
                    videoView.setOnLongClickListener {
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
            if (ViewerHelper.loadAllAtOnce) {
                val target = listOf(data)
                Service.api.asyncDelete(target) {
                    testDataViewModel?.remove(target)
                    viewerViewModel?.remove(target)
                }
            }
            else {
                val target = listOf(data)
                Service.api.asyncDelete(target) {
                    testDataViewModel?.remove(target)
                    viewerViewModel?.remove(target)
                }
            }
        }
    }

    override fun provideView(parent: ViewGroup): View {
        return LayoutIndicatorBinding.inflate(LayoutInflater.from(parent.context), parent, false).also {
            binding = it
            it.pre.setOnClickCallback { viewerViewModel?.setCurrentItem(currentPosition - 1) }
            it.next.setOnClickCallback { viewerViewModel?.setCurrentItem(currentPosition + 1) }
            it.dismiss.setOnClickCallback { viewerViewModel?.dismiss() }
        }.root
    }

    override fun onRelease(viewHolder: RecyclerView.ViewHolder, view: View) {
        viewHolder.itemView.findViewById<View>(R.id.customizeDecor)
                ?.animate()?.setDuration(200)?.alpha(0f)?.start()
        binding?.indicatorDecor?.animate()?.setDuration(200)?.alpha(0f)?.start()
        release()
    }

    override fun onPageSelected(position: Int, viewHolder: RecyclerView.ViewHolder) {
        currentPosition = position
        binding?.indicator?.text = position.toString()
        processSelectVideo(viewHolder)
    }

    private fun processSelectVideo(viewHolder: RecyclerView.ViewHolder) {
        videoTask?.dispose()
        lastVideoVH?.binding?.videoView?.reset()

        when (viewHolder) {
            is VideoViewHolder -> {
                val videoView = viewHolder.binding.videoView
                val task = object : ObserverAdapter<Long>(videoView.lifecycleOwner.lifecycle) {
                    override fun onNext(t: Long) {
                        if (ViewerHelper.simplePlayVideo) {
                            videoView.resume()
                        } else {
                            val playerControlView = viewHolder.itemView.findViewById<PlayerControlView>(R.id.playerControlView)
                            playerControlView?.player = videoView.player()
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

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        val videoView = lastVideoVH?.itemView?.findViewById<ExoVideoView>(R.id.videoView)
        when (event) {
            Lifecycle.Event.ON_RESUME -> videoView?.resume()
            Lifecycle.Event.ON_PAUSE -> videoView?.pause()
            Lifecycle.Event.ON_DESTROY -> {
                videoView?.release()
                videoTask?.dispose()
                videoTask = null
            }
            else -> {}
        }
    }

    private fun release() {
        activity?.lifecycle?.removeObserver(this)
        activity = null
        videoTask?.dispose()
        videoTask = null
        lastVideoVH = null
        binding = null
    }
}
