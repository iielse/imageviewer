package com.github.iielse.imageviewer.widgets.video

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.TextureView
import com.github.iielse.imageviewer.utils.Config
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.analytics.AnalyticsListener
import com.google.android.exoplayer2.source.LoopingMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.util.EventLogger
import com.google.android.exoplayer2.video.VideoListener
import kotlin.math.min

open class ExoVideoView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : TextureView(context, attrs, defStyleAttr) {
    interface VideoRenderedListener {
        fun onRendered(view: ExoVideoView)
    }

    private val exoSourceManager by lazy { ExoSourceManager.newInstance(context, null) }
    private val logger by lazy { EventLogger(null) }
    private var simpleExoPlayer: SimpleExoPlayer? = null
    private var videoRenderedCallback: VideoRenderedListener? = null
    private val listeners = mutableListOf<AnalyticsListener>()
    private var playUrl: String? = null
    protected var prepared = false

    fun prepare(url: String) {
        if (Config.DEBUG) Log.i("viewer", "video prepare $url $simpleExoPlayer")
        playUrl = url
    }

    fun resume() {
        val url = playUrl ?: return
        if (Config.DEBUG) Log.i("viewer", "video resume $url $simpleExoPlayer")
        if (simpleExoPlayer == null) {
            prepared = false
            alpha = 0f
            newSimpleExoPlayer()
            val videoSource: MediaSource = exoSourceManager.getMediaSource(url, true, true, true, context.cacheDir, null)
            simpleExoPlayer?.prepare(LoopingMediaSource(videoSource))
        }
        simpleExoPlayer?.playWhenReady = true
    }

    fun pause() {
        if (Config.DEBUG) Log.i("viewer", "video pause $playUrl $simpleExoPlayer")
        simpleExoPlayer?.playWhenReady = false
    }

    fun reset() {
        if (Config.DEBUG) Log.i("viewer", "video reset $playUrl $simpleExoPlayer")
        simpleExoPlayer?.seekTo(0)
        simpleExoPlayer?.playWhenReady = false
    }

    fun release() {
        val player = simpleExoPlayer ?: return
        if (Config.DEBUG) Log.i("viewer", "video release $playUrl $player")
        player.playWhenReady = false
        player.setVideoTextureView(null)
        player.removeVideoListener(videoListener)
        player.removeAnalyticsListener(logger)
        listeners.toList().forEach { player.removeAnalyticsListener(it) }
        player.release()
        simpleExoPlayer = null
    }

    fun setVideoRenderedCallback(listener: VideoRenderedListener?) {
        videoRenderedCallback = listener
    }

    fun addAnalyticsListener(analyticsListener: AnalyticsListener) {
        if (!listeners.contains(analyticsListener)) {
            listeners.add(analyticsListener)
        }
    }

    fun player(): SimpleExoPlayer? {
        val url = playUrl ?: return null
        if (simpleExoPlayer == null) {
            prepared = false
            alpha = 0f
            newSimpleExoPlayer()
            val videoSource: MediaSource = exoSourceManager.getMediaSource(url, true, true, true, context.cacheDir, null)
            simpleExoPlayer?.prepare(LoopingMediaSource(videoSource))
        }
        return simpleExoPlayer
    }

    private fun newSimpleExoPlayer(): SimpleExoPlayer {
        release()
        if (Config.DEBUG) Log.i("viewer", "video newSimpleExoPlayer $playUrl")
        return SimpleExoPlayer.Builder(context).build().also {
            it.setVideoTextureView(this)
            it.addVideoListener(videoListener)
            if (Config.DEBUG) it.addAnalyticsListener(logger)
            listeners.toList().forEach { userListener -> it.addAnalyticsListener(userListener) }
            simpleExoPlayer = it
        }
    }

    private val videoListener = object : VideoListener {
        override fun onVideoSizeChanged(width: Int, height: Int, unappliedRotationDegrees: Int, pixelWidthHeightRatio: Float) {
            updateTextureViewSize(width, height)
        }
    }

    private fun updateTextureViewSize(videoWidth: Int, videoHeight: Int) {
        val sx = width * 1f / videoWidth
        val sy = height * 1f / videoHeight
        val matrix = android.graphics.Matrix()
        matrix.postScale(videoWidth * 1f / width, videoHeight * 1f / height)
        matrix.postScale(min(sx, sy), min(sx, sy))
        matrix.postTranslate(if (sx > sy) (width - videoWidth * sy) / 2 else 0f,
                if (sx > sy) 0f else (height - videoHeight * sx) / 2)
        setTransform(matrix)
        invalidate()
        alpha = 1f
        videoRenderedCallback?.onRendered(this)
        prepared = true
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (Config.DEBUG) Log.i("viewer", "video onDetachedFromWindow $playUrl $simpleExoPlayer")
        release()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (simpleExoPlayer == null) {
            if (Config.DEBUG) Log.i("viewer", "video onAttachedToWindow $playUrl")
            playUrl?.let(::prepare)
        }
    }
}