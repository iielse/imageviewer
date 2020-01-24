package com.github.iielse.imageviewer

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_imageviewer_photo.*

class PhotoViewHolder(override val containerView: View, callback: PhotoView2.Listener) : RecyclerView.ViewHolder(containerView), LayoutContainer {
    init {
        photoView.setListener(callback)
    }

    fun bind(item: Photo) {
//        photoView.setOnMatrixChangeListener {
//            Log.e("XXX", "OnMatrixChange $it")
//        }
//        photoView.setOnPhotoTapListener { view, x, y ->
//            Log.e("XXX", "OnPhotoTap $x $y")
//        }
//        photoView.setOnOutsidePhotoTapListener {
//            Log.e("XXX", "OnOutsidePhotoTap $it")
//        }
//        photoView.setOnClickListener {
//            Log.e("XXX", "OnClick $it")
//        }
//        photoView.setOnLongClickListener {
//            Log.e("XXX", "OnLongClick $it")
//            true
//        }
//        photoView.setOnScaleChangeListener { scaleFactor, focusX, focusY ->
//            Log.e("XXX", "OnScaleChange $scaleFactor $focusX $focusY")
//        }
//        photoView.setOnSingleFlingListener { e1, e2, vx, vy ->
//            Log.e("XXX", "OnSingleFling $vx $vy")
//            false
//        }

        Glide.with(photoView).load(item.url).into(photoView)
    }
}
