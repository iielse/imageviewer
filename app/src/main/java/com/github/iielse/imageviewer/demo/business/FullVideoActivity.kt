package com.github.iielse.imageviewer.demo.business

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.github.iielse.imageviewer.demo.databinding.FullVideoActivityBinding
import com.github.iielse.imageviewer.demo.utils.setOnClickCallback
import com.github.iielse.imageviewer.widgets.video.ExoVideoView
import java.lang.ref.WeakReference

class FullVideoActivity : AppCompatActivity() {
    private val binding by lazy { FullVideoActivityBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        window.setBackgroundDrawableResource(android.R.color.transparent)
        super.onCreate(savedInstanceState)
        println("FullVideoActivity onCreate $videoView")
        val view = videoView ?: return Unit.also { finish() }
        view.setAutoRelease(false)
        val parent = view.parent as ViewGroup
        parent.removeView(view)
        videoParentRef = WeakReference<ViewGroup>(parent)
        binding.videoLayout.addView(view)
        setContentView(binding.root)

        binding.back.setOnClickCallback {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        videoView?.resume()
        println("FullVideoActivity onResume")
    }

    override fun onPause() {
        super.onPause()
        videoView?.pause()
        println("FullVideoActivity onPause $isFinishing")
        if (isFinishing) release()
    }

    override fun onDestroy() {
        super.onDestroy()
        println("FullVideoActivity onDestroy")
        release()
    }

    override fun finish() {
        super.finish()
        println("FullVideoActivity finish")
        release()
    }

    private var released = false
    private fun release() {
        println("FullVideoActivity release $released")
        if (released) return
        val view = videoView ?: return
        (view.parent as ViewGroup?)?.removeView(view)
        videoParentRef?.get()?.addView(view)
        view.setAutoRelease(true)
        videoParentRef = null
        videoView = null
        released = true
    }

    companion object {
        private var videoParentRef: WeakReference<ViewGroup>? = null
        private var videoView: ExoVideoView? = null

        fun start(context: Context, view: ExoVideoView) {
            videoView = view
            context.startActivity(Intent(context, FullVideoActivity::class.java))
        }
    }
}