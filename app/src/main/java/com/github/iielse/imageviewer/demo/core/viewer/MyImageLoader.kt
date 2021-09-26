package com.github.iielse.imageviewer.demo.core.viewer

import android.net.Uri
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.github.iielse.imageviewer.core.ImageLoader
import com.github.iielse.imageviewer.core.Photo
import com.github.iielse.imageviewer.demo.R
import com.github.iielse.imageviewer.demo.business.ViewerHelper
import com.github.iielse.imageviewer.demo.business.find
import com.github.iielse.imageviewer.demo.core.ObserverAdapter
import com.github.iielse.imageviewer.demo.data.MyData
import com.github.iielse.imageviewer.demo.utils.appContext
import com.github.iielse.imageviewer.demo.utils.lifecycleOwner
import com.github.iielse.imageviewer.demo.utils.toast
import com.github.iielse.imageviewer.utils.Config
import com.github.iielse.imageviewer.widgets.video.ExoVideoView
import com.github.iielse.imageviewer.widgets.video.ExoVideoView2
import com.google.android.exoplayer2.analytics.AnalyticsListener
import com.google.android.exoplayer2.source.LoadEventInfo
import com.google.android.exoplayer2.source.MediaLoadData
import com.google.android.exoplayer2.ui.PlayerControlView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.io.IOException

class MyImageLoader : ImageLoader {
    /**
     * 根据自身photo数据加载图片.可以使用其它图片加载框架.
     */
    override fun load(view: ImageView, data: Photo, viewHolder: RecyclerView.ViewHolder) {
        val it = (data as? MyData?)?.url ?: return
        Glide.with(view).load(it)
                .placeholder(view.drawable)
                .into(view)
    }

    override fun load(exoVideoView: ExoVideoView2, data: Photo, viewHolder: RecyclerView.ViewHolder) {
        val it = (data as? MyData?)?.url ?: return
        val cover = viewHolder.itemView.findViewById<ImageView>(R.id.imageView)
        cover.visibility = View.VISIBLE
        val loadingTask = Runnable {
            findLoadingView(viewHolder)?.visibility = View.VISIBLE
        }
        cover.postDelayed(loadingTask, Config.DURATION_TRANSITION + 1500)
        Glide.with(exoVideoView).load(it)
                .placeholder(cover.drawable)
                .into(cover)

        exoVideoView.addAnalyticsListener(object : AnalyticsListener {
            override fun onLoadError(eventTime: AnalyticsListener.EventTime, loadEventInfo: LoadEventInfo, mediaLoadData: MediaLoadData, error: IOException, wasCanceled: Boolean) {
                findLoadingView(viewHolder)?.visibility = View.GONE
                viewHolder.find<TextView>(R.id.errorPlaceHolder)?.text = error.message
            }
        })
        exoVideoView.setVideoRenderedCallback(object : ExoVideoView.VideoRenderedListener {
            override fun onRendered(view: ExoVideoView) {
                cover.visibility = View.GONE
                cover.removeCallbacks(loadingTask)
                findLoadingView(viewHolder)?.visibility = View.GONE
            }
        })

        val playerControlView = viewHolder.find<PlayerControlView>(R.id.playerControlView)
        exoVideoView.addListener(object : ExoVideoView2.Listener {
            override fun onDrag(view: ExoVideoView2, fraction: Float) {
                if (!ViewerHelper.simplePlayVideo) {
                    playerControlView?.visibility = View.GONE
                }
            }

            override fun onRestore(view: ExoVideoView2, fraction: Float) {
                if (!ViewerHelper.simplePlayVideo) {
                    playerControlView?.visibility = View.VISIBLE
                }
            }

            override fun onRelease(view: ExoVideoView2) {
            }
        })

        exoVideoView.prepare(it)
    }

    /**
     * 根据自身photo数据加载超大图.subsamplingView数据源需要先将内容完整下载到本地.需要注意生命周期
     */
    override fun load(subsamplingView: SubsamplingScaleImageView, data: Photo, viewHolder: RecyclerView.ViewHolder) {
        val it = (data as? MyData?)?.url ?: return
        subsamplingDownloadRequest(it)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { findLoadingView(viewHolder)?.visibility = View.VISIBLE }
                .doFinally { findLoadingView(viewHolder)?.visibility = View.GONE }
                .doOnNext { subsamplingView.setImage(ImageSource.uri(Uri.fromFile(it))) }
                .doOnError { toast(it.message) }
                .subscribe(ObserverAdapter(subsamplingView.lifecycleOwner?.lifecycle))
    }

    private fun subsamplingDownloadRequest(url: String): Observable<File> {
        return Observable.create {
            try {
                it.onNext(Glide.with(appContext).downloadOnly().load(url).submit().get())
                it.onComplete()
            } catch (e: Throwable) {
                if (!it.isDisposed) it.onError(e)
            }
        }
    }

    private fun findLoadingView(viewHolder: RecyclerView.ViewHolder): View? {
        return viewHolder.itemView.findViewById<ProgressBar>(R.id.loadingView)
    }
}
