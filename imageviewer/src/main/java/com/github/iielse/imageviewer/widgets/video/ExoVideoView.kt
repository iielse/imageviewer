package com.github.iielse.imageviewer.widgets.video

import android.content.Context
import android.util.AttributeSet
import android.view.TextureView
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.LoopingMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.video.VideoListener
import kotlin.math.min

open class ExoVideoView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : TextureView(context, attrs, defStyleAttr) {
    interface VideoRenderedListener {
        fun onRendered(view: ExoVideoView)
    }

    private val exoSourceManager by lazy { ExoSourceManager.newInstance(context, null) }
    private var simpleExoPlayer: SimpleExoPlayer? = null
    private var videoRenderedCallback: VideoRenderedListener? = null
    private var playUrl: String? = null

    fun prepare(url: String) {
        playUrl = url
        if (simpleExoPlayer == null) {
            alpha = 0f
            newSimpleExoPlayer()
            val videoSource: MediaSource = exoSourceManager.getMediaSource(url, true, true, true, context.cacheDir, null)
            simpleExoPlayer?.prepare(LoopingMediaSource(videoSource))
        }
    }

    fun resume() {
        if (simpleExoPlayer == null) {
            playUrl?.let(::prepare)
        }
        simpleExoPlayer?.playWhenReady = true
    }

    fun pause() {
        simpleExoPlayer?.playWhenReady = false
    }

    fun reset() {
        simpleExoPlayer?.seekTo(0)
        simpleExoPlayer?.playWhenReady = false
    }

    fun release() {
        simpleExoPlayer?.playWhenReady = false
        simpleExoPlayer?.removeVideoListener(videoListener)
        simpleExoPlayer?.release()
        simpleExoPlayer = null
    }

    fun setVideoRenderedCallback(listener: VideoRenderedListener?) {
        videoRenderedCallback = listener
    }

    fun player() = simpleExoPlayer

    private fun newSimpleExoPlayer(): SimpleExoPlayer {
        release()
        return SimpleExoPlayer.Builder(context).build().also {
            it.setVideoTextureView(this)
            it.addVideoListener(videoListener)
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
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        release()
    }
}