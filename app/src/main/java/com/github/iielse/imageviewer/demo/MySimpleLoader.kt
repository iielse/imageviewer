package com.github.iielse.imageviewer.demo

import android.app.Activity
import android.graphics.Bitmap
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.github.iielse.imageviewer.core.ImageLoader
import com.github.iielse.imageviewer.core.Photo
import java.io.File

class MySimpleLoader : ImageLoader {
    override fun load(view: ImageView, data: Photo, viewHolder: RecyclerView.ViewHolder) {
        if (data.id() == 3L) {
            testLoading(view, data, viewHolder)
            return
        }
        if (data.id() == 9L || data.id() == 4L) {
            // 9L url png // 4L url gif
            (data as? MyData?)?.url?.let { if (it.isNotEmpty()) Glide.with(view).load(it).into(view) }
            return
        }

        Glide.with(view).load(provideBitmap(data.id())).into(view)
    }

    override fun load(subsamplingView: SubsamplingScaleImageView, data: Photo, viewHolder: RecyclerView.ViewHolder) {
        runOnIOThread {
            val fileName = "subsampling_${data.id()}"
            val file = File(appContext().cacheDir, fileName)
            if (!file.exists()) {
                val s = provideBitmap(data.id())
                saveBitmapFile(s, file)
            }

            runOnUIThread {
                subsamplingView.setImage(ImageSource.uri(Uri.fromFile(file)))
            }
        }
    }
}


private fun testLoading(view: ImageView, data: Photo, viewHolder: RecyclerView.ViewHolder) {
    Glide.with(view).clear(view)
    val loading = viewHolder.itemView.findViewById<ProgressBar>(R.id.loading)
    loading?.visibility = View.VISIBLE
    Handler(Looper.getMainLooper()).postDelayed({
        // 开发者自己管理好生命周期.
        if ((view.context as? Activity?)?.isFinishing == true) return@postDelayed

        Glide.with(view)
                .asBitmap()
                .load("https://www.google.cn/landing/cnexp/google-search.png")
                .addListener(object : RequestListener<Bitmap> {
                    override fun onResourceReady(resource: Bitmap?, model: Any?, target: Target<Bitmap>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        loading?.visibility = View.GONE
                        return false
                    }

                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Bitmap>?, isFirstResource: Boolean): Boolean {
                        toast("13 load failed")
                        loading?.visibility = View.GONE
                        return false
                    }
                })
                .into(view)
    }, 5000)
}