package github.iielse.imageviewer.demo

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import ch.ielse.demo.p02.R
import com.bumptech.glide.Glide
import com.github.iielse.imageviewer.ImageViewerBuilder
import com.github.iielse.imageviewer.core.DataProviderAdapter
import com.github.iielse.imageviewer.core.Transformer
import com.github.iielse.imageviewer.core.Photo
import com.github.iielse.imageviewer.core.ImageLoader
import com.github.iielse.imageviewer.utils.log
import kotlinx.android.synthetic.main.activity_9.*

class MainActivity6 : AppCompatActivity() {
    private val init100 = MyPhoto(id = 100, url = "")
    private val init22 = MyPhoto(id = 22, url = "")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_9)

        Glide.with(pView)
                .load(provideBitmap(init100.id))
                .into(pView)
        Glide.with(pView22)
                .load(provideBitmap(init22.id))
                .into(pView22)

        pView.setOnClickListener {
            builder(init100, pView).show()
        }
        pView22.setOnClickListener {
            builder(init22, pView22).show()
        }
    }

    private fun builder(clicked: MyPhoto, clickedView: ImageView): ImageViewerBuilder {
        return ImageViewerBuilder(
                context = this,
                dataProvider = object : DataProviderAdapter() {
                    override fun loadInitial(): List<Photo> {
                        return listOf(clicked)
                    }

                    override fun loadAfter(key: Int, callback: (List<Photo>) -> Unit) {
                        fetchAfter(key, callback)
                    }

                    override fun loadBefore(key: Int, callback: (List<Photo>) -> Unit) {
                        fetchBefore(key, callback)
                    }
                },
                transformer = object : Transformer {
                    override fun getView(key: Int): ImageView? {
                        log { "getView key $key" }
                        return when (key) {
                            clicked.id -> clickedView
                            else -> null
                        }
                    }
                },
                imageLoader = object : ImageLoader {
                    override fun load(view: ImageView, photo: Photo) {
                        Glide.with(view)
                                .load(provideBitmap(photo.id()))
                                .into(view)
                    }
                },
                initKey = clicked.id
        )
    }
}
