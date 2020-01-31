package com.github.iielse.imageviewer.demo

import android.graphics.Bitmap
import android.net.Uri
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.github.iielse.imageviewer.core.ImageLoader
import com.github.iielse.imageviewer.core.Photo
import java.io.File

class MySimpleLoader : ImageLoader {
    override fun load(view: ImageView, data: Photo, viewHolder: RecyclerView.ViewHolder) {
        val it =  (data as? MyData?)?.url!!
        Glide.with(view).load(it).into(view)
    }

    override fun load(subsamplingView: SubsamplingScaleImageView, data: Photo, viewHolder: RecyclerView.ViewHolder) {
        runOnIOThread {
            val fileName = "data_subsampling_${data.id()}"
            val file = File(appContext().cacheDir, fileName)
            if (!file.exists()) {
                val it =  (data as? MyData?)?.url!!
                val s : Bitmap = Glide.with(appContext()).asBitmap().load(it).submit().get()
                saveBitmapFile(s, file)
            }

            runOnUIThread {
                subsamplingView.setImage(ImageSource.uri(Uri.fromFile(file)))
            }
        }
    }
}
