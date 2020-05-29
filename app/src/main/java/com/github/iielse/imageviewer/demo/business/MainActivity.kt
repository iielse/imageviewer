package com.github.iielse.imageviewer.demo.business

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.github.iielse.imageviewer.demo.R
import com.github.iielse.imageviewer.demo.core.ITEM_CLICKED
import com.github.iielse.imageviewer.demo.core.viewer.TransitionViewsRef
import com.github.iielse.imageviewer.demo.core.viewer.TransitionViewsRef.KEY_MAIN
import com.github.iielse.imageviewer.demo.data.MyData
import com.github.iielse.imageviewer.demo.utils.App
import com.github.iielse.imageviewer.demo.utils.statusBarHeight
import com.github.iielse.imageviewer.utils.Config
import kotlinx.android.synthetic.main.main_activity.*

class MainActivity : AppCompatActivity() {
    private val viewModel by lazy { ViewModelProvider(this).get(TestDataViewModel::class.java) }
    private val viewerHelper by lazy { ViewerHelper() }
    private val adapter by lazy { TestDataAdapter() }

    override fun onDestroy() {
        super.onDestroy()
        orientation.setOnClickListener(null)
        fullScreen.setOnClickListener(null)
        loadAllAtOnce.setOnClickListener(null)
        recyclerView.adapter = null
        adapter.setListener(null)
        TransitionViewsRef.releaseTransitionViewRef(KEY_MAIN)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.context = this.applicationContext // 随便找位置借个全局context用用.
        Config.TRANSITION_OFFSET_Y = statusBarHeight()
        setContentView(R.layout.main_activity)
        initialViews()
        viewModel.dataList.observe(this, androidx.lifecycle.Observer(adapter::submitList))
    }

    private fun handleAdapterListener(action: String, item: Any?) {
        when (action) {
            ITEM_CLICKED -> (item as? MyData?)?.let(::showViewer)
        }
    }

    private fun showViewer(item: MyData) {
        viewerHelper.provideImageViewerBuilder(this, item)
                .show()
    }

    private fun initialViews() {
        orientation.setOnClickListener {
            var orientationH = it.tag as? Boolean? ?: true
            orientationH = !orientationH
            orientation.text = if (orientationH) "Horizontal" else "Vertical"
            Config.VIEWER_ORIENTATION = if (orientationH) ViewPager2.ORIENTATION_HORIZONTAL else ViewPager2.ORIENTATION_VERTICAL
            it.tag = orientationH
        }
        fullScreen.setOnClickListener {
            val isFullScreen = viewerHelper.fullScreen
            viewerHelper.fullScreen = !isFullScreen
            fullScreen.text = if (!isFullScreen) "FullScreen(on)" else "FullScreen(off)"
            Config.TRANSITION_OFFSET_Y = if (!isFullScreen) 0 else statusBarHeight()
            it.tag = !isFullScreen
        }
        loadAllAtOnce.setOnClickListener {
            val isLoadAllAtOnce = viewerHelper.loadAllAtOnce
            viewerHelper.loadAllAtOnce = !isLoadAllAtOnce
            loadAllAtOnce.text = if (!isLoadAllAtOnce) "LoadAllAtOnce(on)" else "LoadAllAtOnce(off)"
            it.tag = !isLoadAllAtOnce
        }

        recyclerView.layoutManager = GridLayoutManager(this, 3)
        recyclerView.adapter = adapter
        adapter.setListener(::handleAdapterListener)
    }
}

