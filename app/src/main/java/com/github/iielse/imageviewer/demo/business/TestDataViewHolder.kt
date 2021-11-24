package com.github.iielse.imageviewer.demo.business

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.github.iielse.imageviewer.demo.core.AdapterCallback
import com.github.iielse.imageviewer.demo.core.ITEM_CLICKED
import com.github.iielse.imageviewer.demo.data.MyData
import com.github.iielse.imageviewer.demo.databinding.ItemImageBinding

class TestDataViewHolder(
    parent: ViewGroup,
    callback: AdapterCallback,
    val binding: ItemImageBinding =
        ItemImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
) : RecyclerView.ViewHolder(binding.root) {
    init {
        // 初始化点击回调
        itemView.setOnClickListener {
            (it.tag as? MyData?)?.let { callback.invoke(ITEM_CLICKED, it) }
        }
    }

    fun bind(item: MyData, pos: Int) {
        itemView.tag = item

        binding.pos.text = when {
            item.subsampling -> "$pos subsampling"
            item.url.endsWith(".gif") -> "$pos gif"
            item.url.endsWith(".mp4") -> "$pos video"
            else -> pos.toString()
        }

        // 测试 fitXY 的过渡动画效果
        binding.imageView.scaleType = if (pos == 19) ImageView.ScaleType.FIT_XY else ImageView.ScaleType.CENTER_CROP

        Glide.with(binding.imageView).load(item.url).into(binding.imageView)
    }
}

