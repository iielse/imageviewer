package com.github.iielse.imageviewer.viewholders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.iielse.imageviewer.ImageViewerAdapterListener
import com.github.iielse.imageviewer.R
import com.github.iielse.imageviewer.adapter.ItemType
import com.github.iielse.imageviewer.core.Components.requireImageLoader
import com.github.iielse.imageviewer.core.Components.requireVHCustomizer
import com.github.iielse.imageviewer.core.Photo
import com.github.iielse.imageviewer.databinding.ItemImageviewerPhotoBinding
import com.github.iielse.imageviewer.widgets.PhotoView2

class PhotoViewHolder(
    parent: ViewGroup,
    callback: ImageViewerAdapterListener,
    val binding: ItemImageviewerPhotoBinding =
        ItemImageviewerPhotoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
) : RecyclerView.ViewHolder(binding.root) {
    init {
        binding.photoView.setListener(object : PhotoView2.Listener {
            override fun onDrag(view: PhotoView2, fraction: Float) = callback.onDrag(this@PhotoViewHolder, view, fraction)
            override fun onRestore(view: PhotoView2, fraction: Float) = callback.onRestore(this@PhotoViewHolder, view, fraction)
            override fun onRelease(view: PhotoView2) = callback.onRelease(this@PhotoViewHolder, view)
        })
        requireVHCustomizer().initialize(ItemType.PHOTO, this)
    }

    fun bind(item: Photo) {
        binding.photoView.setTag(R.id.viewer_adapter_item_key, item.id())
        binding.photoView.setTag(R.id.viewer_adapter_item_data, item)
        binding.photoView.setTag(R.id.viewer_adapter_item_holder, this)
        requireVHCustomizer().bind(ItemType.PHOTO, item, this)
        requireImageLoader().load(binding.photoView, item, this)
    }
}