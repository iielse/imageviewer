package github.iielse.imageviewer.demo

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import ch.ielse.demo.p02.R
import ch.ielse.demo.p02.TestActivity
import com.bumptech.glide.Glide
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.github.iielse.imageviewer.ImageViewerBuilder
import com.github.iielse.imageviewer.core.*
import com.github.iielse.imageviewer.utils.inflate
import com.github.iielse.imageviewer.utils.log
import com.github.iielse.imageviewer.widgets.PhotoView2
import kotlinx.android.synthetic.main.activity_9.*
import kotlinx.android.synthetic.main.item_photo_custom_layout.*
import java.io.File

class MainActivity6 : AppCompatActivity() {
    private val init100 = MyViewerData(id = 101, url = "")
    private val initx = MyViewerData(id = 0, url = "")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_9)
        toTest.setOnClickListener { startActivity(Intent(this, TestActivity::class.java)) }

        Glide.with(pView)
                .load(provideBitmap(init100.id))
                .into(pView)
        Glide.with(pViewx)
                .load(provideBitmap(initx.id))
                .into(pViewx)

        pView.setOnClickListener {
            builder(init100, pView).show()
        }
        pViewx.setOnClickListener {
            builder(initx, pViewx).show()
        }
    }

    private fun builder(clicked: MyViewerData, clickedView: ImageView): ImageViewerBuilder {
        return ImageViewerBuilder(
                context = this,
                dataProvider = object : DataProviderAdapter() {
                    override fun loadInitial(): List<Photo> {
                        return listOf(clicked)
                    }

                    override fun loadAfter(key: Long, callback: (List<Photo>) -> Unit) {
                        fetchAfter(key, callback)
                    }

                    override fun loadBefore(key: Long, callback: (List<Photo>) -> Unit) {
                        fetchBefore(key, callback)
                    }
                },
                transformer = object : Transformer {
                    override fun getView(key: Long): ImageView? {
                        log { "getView key $key" }
                        return when (key) {
                            clicked.id -> clickedView
                            else -> null
                        }
                    }
                },
                imageLoader = object : ImageLoader {
                    override fun load(view: ImageView, data: Photo) {
                        Glide.with(view)
                                .load(provideBitmap(data.id()))
                                .into(view)
                    }

                    override fun load(subsamplingView: SubsamplingScaleImageView, data: Photo) {
                        runOnIOThread {
                            val fileName = "subsampling_${data.id()}"
                            val file = File(cacheDir, fileName)//将要保存图片的路径
                            if (!file.exists()) {
                                val s = provideBitmap(data.id())
                                saveBitmapFile(s, file)
                            }
                            runOnUIThread {
                                subsamplingView.setImage(ImageSource.uri(Uri.fromFile(file)))
                            }
                        }
                    }
                },
                initKey = clicked.id
        )
                .setVHCustomizer(object : VHCustomizer {
                    override fun initialize(type: Int, viewHolder: RecyclerView.ViewHolder) {
                        (viewHolder.itemView as? ViewGroup?)?.let {
                            it.addView(it.inflate(R.layout.item_photo_custom_layout))
                        }
                    }

                    override fun bind(type: Int, data: Photo, viewHolder: RecyclerView.ViewHolder) {
                        (viewHolder.itemView as? ViewGroup?)?.let {
                            val x = data as MyViewerData
                            it.findViewById<TextView>(R.id.exText).text = x.desc
                        }
                    }
                })
                .setViewerCallback(object : ViewerCallbackAdapter() {
                    override fun onInit(viewHolder: RecyclerView.ViewHolder) {
                    }

                    override fun onRestore(viewHolder: RecyclerView.ViewHolder, view: PhotoView2, fraction: Float) {
                    }

                    override fun onDrag(viewHolder: RecyclerView.ViewHolder, view: PhotoView2, fraction: Float) {
                    }

                    override fun onRelease(viewHolder: RecyclerView.ViewHolder, view: View) {
                        viewHolder.itemView.findViewById<View>(R.id.customizeDecor)
                                .animate().setDuration(200).alpha(0f).start()
                    }

                    override fun onPageSelected(position: Int) {
                        log { "onPageSelected $position" }
                    }
                })
    }
}