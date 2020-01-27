package github.iielse.imageviewer.demo

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import ch.ielse.demo.p02.R
import ch.ielse.demo.p02.TestActivity
import com.bumptech.glide.Glide
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.github.iielse.imageviewer.ImageViewerBuilder
import com.github.iielse.imageviewer.core.DataProviderAdapter
import com.github.iielse.imageviewer.core.Transformer
import com.github.iielse.imageviewer.core.Photo
import com.github.iielse.imageviewer.core.ImageLoader
import com.github.iielse.imageviewer.utils.log
import kotlinx.android.synthetic.main.activity_9.*
import java.io.File

class MainActivity6 : AppCompatActivity() {
    private val init100 = MyPhoto(id = 100, url = "")
    private val initx = MyPhoto(id = 0, url = "")

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

    private fun builder(clicked: MyPhoto, clickedView: ImageView): ImageViewerBuilder {
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
                imageLoader = object : ImageLoader<MyPhoto> {
                    override fun load(view: ImageView, data: MyPhoto) {
                        Glide.with(view)
                                .load(provideBitmap(data.id()))
                                .into(view)
                    }

                    override fun load(subsamplingView: SubsamplingScaleImageView, data: MyPhoto) {
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
    }
}
