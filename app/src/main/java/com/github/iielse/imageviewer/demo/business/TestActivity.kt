package com.github.iielse.imageviewer.demo.business

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.iielse.imageviewer.demo.R
import com.github.iielse.imageviewer.demo.utils.toast
import kotlinx.android.synthetic.main.activity_test.*

class TestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        exoVideoView.setOnClickListener {
            toast("video click")
        }
        exoVideoView.setOnLongClickListener {
            toast("video long clicked")
            true
        }
        exoVideoView.prepare("https://9890.vod.myqcloud.com/9890_4e292f9a3dd011e6b4078980237cc3d3.f20.mp4")
        exoVideoView.resume()
    }

    override fun onDestroy() {
        super.onDestroy()
        exoVideoView.release()
    }

}
