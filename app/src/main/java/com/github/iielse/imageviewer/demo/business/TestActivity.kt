package com.github.iielse.imageviewer.demo.business

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.iielse.imageviewer.demo.databinding.ActivityTestBinding
import com.github.iielse.imageviewer.demo.utils.toast
import com.github.iielse.imageviewer.widgets.video.ExoVideoView
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DataSpec
import com.google.android.exoplayer2.upstream.FileDataSource
import java.io.File

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
        // val url = "https://9890.vod.myqcloud.com/9890_4e292f9a3dd011e6b4078980237cc3d3.f20.mp4"
        val url = File("/storage/emulated/0/DCIM/Camera/trailer.mp4").absolutePath
        binding.exoVideoView.prepare(url)
        binding.exoVideoView.resume(localMediaSourceProvider)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.exoVideoView.release()
    }

}


val localMediaSourceProvider = object : ExoVideoView.MediaSourceProvider {
    override fun provide(playUrl: String): List<MediaSource>? {
        return try {
//            val dataSpec = DataSpec(Uri.fromFile(File(playUrl)))
//            val fileDataSource = FileDataSource()
//            fileDataSource.open(dataSpec)
//            val factory = DataSource.Factory { fileDataSource }
//            val mediaSource =
//                ProgressiveMediaSource.Factory(factory, DefaultExtractorsFactory())
//                    .createMediaSource(MediaItem.Builder().setUri(fileDataSource.uri).build())
//            listOf(mediaSource)
            null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
