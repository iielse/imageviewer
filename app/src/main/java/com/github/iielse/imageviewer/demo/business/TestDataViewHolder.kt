package com.github.iielse.imageviewer.demo.business

import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.github.iielse.imageviewer.demo.core.AdapterCallback
import com.github.iielse.imageviewer.demo.core.ITEM_CLICKED
import com.github.iielse.imageviewer.demo.data.MyData
import com.github.iielse.imageviewer.utils.log
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_image.*
import kotlinx.android.synthetic.main.item_image.view.*

class TestDataViewHolder(override val containerView: View, private val listener: AdapterCallback) : RecyclerView.ViewHolder(containerView), LayoutContainer {
    init {
        // 初始化点击回调
        itemView.setOnClickListener {
            (it.tag as? MyData?)?.let { listener.invoke(ITEM_CLICKED, it) }
        }
    }

    fun bind(item: MyData, pos: Int) {
        itemView.tag = item

        posTxt.text = when {
            item.subsampling -> "$pos subsampling"
            item.url.endsWith(".gif") -> "$pos gif"
            item.url.endsWith(".mp4") -> "$pos video"
            else -> pos.toString()
        }

        // 测试 fitXY 的过渡动画效果
        itemView.imageView.scaleType = if (pos == 19) ImageView.ScaleType.FIT_XY else ImageView.ScaleType.CENTER_CROP

        Glide.with(imageView).load(item.url).into(imageView)
        log { "DataViewHolder bind ${item.id}" }
    }
}

