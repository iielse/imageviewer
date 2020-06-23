package com.github.iielse.imageviewer.widgets.video

import android.content.Context
import android.util.AttributeSet
import android.view.TextureView
import com.github.iielse.imageviewer.utils.log
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

    fun prepare(url: String) {
        alpha = 0f
        newSimpleExoPlayer()
        val videoSource: MediaSource = exoSourceManager.getMediaSource(url, true, true, false, context.cacheDir, null)
        simpleExoPlayer?.prepare(LoopingMediaSource(videoSource))
    }

    fun pause() {
        simpleExoPlayer?.playWhenReady = false
    }

    fun resume() {
        simpleExoPlayer?.playWhenReady = true
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

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        release()
    }

    private fun updateTextureViewSize(videoWidth: Int, videoHeight: Int) {
        log { "updateTextureViewSize width $width height $height videoWidth $videoWidth videoHeight $videoHeight" }
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

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        log { "onLayout changed $changed left $left top $top right $right bottom $bottom" }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        log { "onMeasure widthMeasureSpec $widthMeasureSpec heightMeasureSpec $heightMeasureSpec" }
    }
}