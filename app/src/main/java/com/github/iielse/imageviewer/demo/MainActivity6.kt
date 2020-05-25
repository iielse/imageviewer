package com.github.iielse.imageviewer.demo

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagedListAdapter
import androidx.paging.toLiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.github.iielse.imageviewer.ImageViewerBuilder
import com.github.iielse.imageviewer.ImageViewerDialogFragment
import com.github.iielse.imageviewer.demo.data.MyData
import com.github.iielse.imageviewer.demo.data.dataSourceFactory
import com.github.iielse.imageviewer.demo.data.myData
import com.github.iielse.imageviewer.demo.utils.X
import com.github.iielse.imageviewer.demo.utils.statusBarHeight
import com.github.iielse.imageviewer.demo.viewer.*
import com.github.iielse.imageviewer.utils.Config
import com.github.iielse.imageviewer.utils.inflate
import com.github.iielse.imageviewer.utils.log
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.activity_10.*
import kotlinx.android.synthetic.main.item_image.*
import kotlinx.android.synthetic.main.item_image.view.*
import java.util.*

class MainActivity6 : AppCompatActivity() {
    private val viewModel by lazy { ViewModelProvider(this).get(DataViewModel::class.java) }
    private val myCustomController by lazy { MyCustomController(this) }
    private val adapter by lazy { DataAdapter() }

    override fun onDestroy() {
        super.onDestroy()
        Trans.mapping.clear()
        adapter.setListener(null)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        X.appContext = this.applicationContext
        Config.TRANSITION_OFFSET_Y = statusBarHeight()
        setContentView(R.layout.activity_10)
        orientation.setOnClickListener {
            var orientationH = it.tag as? Boolean? ?: true
            orientationH = !orientationH
            orientation.text = if (orientationH) "HORIZONTAL" else "VERTICAL"
            Config.VIEWER_ORIENTATION = if (orientationH) ViewPager2.ORIENTATION_HORIZONTAL else ViewPager2.ORIENTATION_VERTICAL
            it.tag = orientationH
        }
        fullScreen.setOnClickListener {
            var isFullScreen = it.tag as? Boolean? ?: false
            isFullScreen = !isFullScreen
            fullScreen.text = if (isFullScreen) "FullScreen(on)" else "FullScreen(off)"
            Config.TRANSITION_OFFSET_Y = if (isFullScreen) 0 else statusBarHeight()
            it.tag = isFullScreen
        }

        recyclerView.layoutManager = GridLayoutManager(this, 3)
        recyclerView.adapter = adapter

        adapter.setListener(::handleAdapterListener)
        viewModel.dataList.observe(this, androidx.lifecycle.Observer(adapter::submitList))
    }

    private fun handleAdapterListener(action: String, item: Any?) {
        when (action) {
            ITEM_CLICKED -> (item as? MyData?)?.let(::showViewer)
        }
    }

    private fun showViewer(item: MyData) {
        if (item.id == 30L) {
            startActivity(Intent(this, MainJavaActivity7::class.java))
            return
        }

        builder(item).show()
    }

    private fun builder(clickedData: MyData): ImageViewerBuilder {
        return ImageViewerBuilder(
                context = this,
                initKey = clickedData.id,
                dataProvider = MyTestDataProvider(clickedData),
                imageLoader = MySimpleLoader(),
                transformer = MyTransformer()
        ).also {
            myCustomController.init(it)
            if (fullScreen.tag as? Boolean? == true) {
                it.setViewerFactory(object : ImageViewerDialogFragment.Factory() {
                    override fun build(): ImageViewerDialogFragment {
                        return FullScreenImageViewerDialogFragment()
                    }
                })
            }
        }
    }
}

typealias AdapterCallback = (action: String, item: Any?) -> Unit

class DataAdapter : PagedListAdapter<MyData, RecyclerView.ViewHolder>(diff1) {
    private var callback: AdapterCallback? = null

    fun setListener(callback: AdapterCallback? = null) {
        this.callback = callback
    }

    private val listener = object : AdapterCallback {
        override fun invoke(action: String, item: Any?) {
            callback?.invoke(action, item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return DataViewHolder(parent.inflate(R.layout.item_image), listener)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (holder) {
            is DataViewHolder -> item?.let { holder.bind(it, position) }
        }
    }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        super.onViewAttachedToWindow(holder)
        if (holder is DataViewHolder) {
            (holder.itemView.tag as? MyData?)?.let {
                log { "DataViewHolder onViewAttachedToWindow ${it.id}" }
                Trans.mapping.put(it.id, holder.imageView)
            }
        }
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        if (holder is DataViewHolder) {
            (holder.itemView.tag as? MyData?)?.let {
                log { "DataViewHolder onViewDetachedFromWindow ${it.id}" }
                Trans.mapping.remove(it.id)
            }
        }

    }
}

const val ITEM_CLICKED = "item_clicked"

class DataViewHolder(override val containerView: View, private val listener: AdapterCallback) : RecyclerView.ViewHolder(containerView), LayoutContainer {
    init {
        itemView.setOnClickListener {
            (it.tag as? MyData?)?.let { listener.invoke(ITEM_CLICKED, it) }
        }
    }

    fun bind(item: MyData, pos: Int) {
        itemView.tag = item
        posTxt.text = "$pos ${if (item.subsampling) "subsampling" else ""}"
        if (pos == 19) {
            itemView.imageView.scaleType = ImageView.ScaleType.FIT_XY
        } else {
            itemView.imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        }
        Glide.with(imageView).load(item.url).into(imageView)
        log { "DataViewHolder bind ${item.id}" }
    }
}

private val diff1 = object : DiffUtil.ItemCallback<MyData>() {
    override fun areItemsTheSame(
            oldItem: MyData,
            newItem: MyData
    ): Boolean {
        return newItem.id == oldItem.id
    }

    override fun areContentsTheSame(
            oldItem: MyData,
            newItem: MyData
    ): Boolean {
        return newItem.id == oldItem.id
                && Objects.equals(newItem.url, oldItem.url)
                && Objects.equals(newItem.desc, oldItem.desc)
    }
}


class DataViewModel : ViewModel() {
    val dataList = dataSourceFactory().toLiveData(pageSize = 1)
}