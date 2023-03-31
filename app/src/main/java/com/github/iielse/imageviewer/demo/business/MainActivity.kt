package com.github.iielse.imageviewer.demo.business

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.github.iielse.imageviewer.demo.core.ITEM_CLICKED
import com.github.iielse.imageviewer.demo.data.MyData
import com.github.iielse.imageviewer.demo.databinding.MainActivityBinding
import com.github.iielse.imageviewer.demo.utils.App
import com.github.iielse.imageviewer.demo.utils.statusBarHeight
import com.github.iielse.imageviewer.utils.Config
import com.github.iielse.imageviewer.widgets.video.ExoVideoView

class MainActivity : AppCompatActivity() {
    private val binding by lazy { MainActivityBinding.inflate(layoutInflater) }
    private val viewModel by viewModels<TestDataViewModel> { TestDataViewModel.Factory() }
    private val adapter by lazy { TestDataAdapter() }

    override fun onDestroy() {
        super.onDestroy()
        binding.orientation.setOnClickListener(null)
        binding.fullScreen.setOnClickListener(null)
        binding.loadAllAtOnce.setOnClickListener(null)
        binding.customTransition.setOnClickListener(null)
        binding.recyclerView.adapter = null
        adapter.setListener(null)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.context = this.applicationContext // 随便找位置借个全局context用用.
        Config.TRANSITION_OFFSET_Y = statusBarHeight()
        // Config.TRANSITION_OFFSET_X = statusBarHeight() // android:screenOrientation="landscape"
        setContentView(binding.root)
        initialViews()
        viewModel.dataList.observe(this, androidx.lifecycle.Observer(adapter::submitList))

        viewModel.request()
    }

    private fun handleAdapterListener(action: String, item: Any?) {
        when (action) {
            ITEM_CLICKED -> showViewer(item as? MyData?)
        }
    }

    private fun showViewer(item: MyData?) {
        if (item == null) return
//        if (item.id == 10L) {
//            startActivity(Intent(this, TestActivity::class.java))
//            return
//        }
        ViewerHelper.provideImageViewerBuilder(this, item)
                .show()
    }

    private fun initialViews() {
        binding.orientation.setOnClickListener {
            val orientationH = ViewerHelper.orientationH
            ViewerHelper.orientationH = !orientationH
            binding.orientation.text = if (!orientationH) "Horizontal" else "Vertical"
            Config.VIEWER_ORIENTATION = if (!orientationH) ViewPager2.ORIENTATION_HORIZONTAL else ViewPager2.ORIENTATION_VERTICAL
        }
        binding.fullScreen.setOnClickListener {
            val isFullScreen = ViewerHelper.fullScreen
            ViewerHelper.fullScreen = !isFullScreen
            binding.fullScreen.text = if (!isFullScreen) "FullScreen(on)" else "FullScreen(off)"
            Config.TRANSITION_OFFSET_Y = if (!isFullScreen) 0 else statusBarHeight()
        }
        binding.loadAllAtOnce.setOnClickListener {
            val isLoadAllAtOnce = ViewerHelper.loadAllAtOnce
            ViewerHelper.loadAllAtOnce = !isLoadAllAtOnce
            binding.loadAllAtOnce.text = if (!isLoadAllAtOnce) "LoadAllAtOnce(on)" else "LoadAllAtOnce(off)"
        }
        binding.simplePlayVideo.setOnClickListener {
            val isSimplePlayVideo = ViewerHelper.simplePlayVideo
            ViewerHelper.simplePlayVideo = !isSimplePlayVideo
            binding.simplePlayVideo.text = if (!isSimplePlayVideo) "Video(simple)" else "Video(controlView)"
        }
        binding.videoScaleType.setOnClickListener {
            when(ViewerHelper.videoScaleType) {
                ExoVideoView.SCALE_TYPE_FIT_XY -> {
                    ViewerHelper.videoScaleType = ExoVideoView.SCALE_TYPE_CENTER_CROP
                    binding.videoScaleType.text = "videoScaleType(centerCrop)"
                    Config.VIDEO_SCALE_TYPE = ExoVideoView.SCALE_TYPE_CENTER_CROP
                }
                ExoVideoView.SCALE_TYPE_CENTER_CROP -> {
                    ViewerHelper.videoScaleType = ExoVideoView.SCALE_TYPE_FIT_CENTER
                    binding.videoScaleType.text = "videoScaleType(fitCenter)"
                    Config.VIDEO_SCALE_TYPE = ExoVideoView.SCALE_TYPE_FIT_CENTER
                }
                ExoVideoView.SCALE_TYPE_FIT_CENTER -> {
                    ViewerHelper.videoScaleType = ExoVideoView.SCALE_TYPE_FIT_XY
                    binding.videoScaleType.text = "videoScaleType(fitXY)"
                    Config.VIDEO_SCALE_TYPE = ExoVideoView.SCALE_TYPE_FIT_XY
                }
            }
        }
        binding.customTransition.setOnClickListener {
            CustomTransitionHelper.show(it)
        }
        binding.recyclerView.layoutManager = GridLayoutManager(this, 3)
        binding.recyclerView.adapter = adapter
        adapter.setListener(::handleAdapterListener)
    }
}

