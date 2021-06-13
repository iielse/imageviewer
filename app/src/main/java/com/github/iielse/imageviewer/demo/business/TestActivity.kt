package com.github.iielse.imageviewer.demo.business

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.iielse.imageviewer.demo.databinding.ActivityTestBinding
import com.github.iielse.imageviewer.demo.utils.toast

class TestActivity : AppCompatActivity() {
    private val binding by lazy { ActivityTestBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.exoVideoView.setOnClickListener {
            toast("video click")
        }
        binding.exoVideoView.setOnLongClickListener {
            toast("video long clicked")
            true
        }
        binding.exoVideoView.prepare("https://9890.vod.myqcloud.com/9890_4e292f9a3dd011e6b4078980237cc3d3.f20.mp4")
        binding.exoVideoView.resume()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.exoVideoView.release()
    }

}
