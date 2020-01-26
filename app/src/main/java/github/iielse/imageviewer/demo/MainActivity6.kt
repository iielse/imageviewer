package github.iielse.imageviewer.demo

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import ch.ielse.demo.p02.R
import com.bumptech.glide.Glide
import com.github.iielse.imageviewer.ImageViewerBuilder
import com.github.iielse.imageviewer.DataProviderAdapter
import com.github.iielse.imageviewer.Transformer
import com.github.iielse.imageviewer.Photo
import com.github.iielse.imageviewer.`interface`.ImageLoader
import kotlinx.android.synthetic.main.activity_9.*

class MainActivity6 : AppCompatActivity() {
    private val init = Photo(id = 100, url = "", height = 0, width = 0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_9)

        Glide.with(pView)
                .load(provideBitmap(init.id))
                .into(pView)

        pView.setOnClickListener {
            builder().show()
        }
    }

    private fun builder(): ImageViewerBuilder {
        return ImageViewerBuilder(
                context = this,
                dataProvider = object : DataProviderAdapter() {
                    override fun loadInitial(): List<Photo> {
                        return listOf(init)
                    }

                    override fun loadAfter(key: Int, callback: (List<Photo>) -> Unit) {
                        fetchAfter(key, callback)
                    }

                    override fun loadBefore(key: Int, callback: (List<Photo>) -> Unit) {
                        fetchBefore(key, callback)
                    }
                },
                transformer = object : Transformer {
                    override fun getView(pos: Int): ImageView? {
                        return when (pos) {
                            init.id -> pView
                            else -> null
                        }
                    }
                },
                imageLoader = object : ImageLoader {
                    override fun load(view: ImageView, photo: Photo) {
                        Glide.with(view)
                                .load(provideBitmap(photo.id))
                                .into(view)
                    }
                },
                initialPosition = init.id
        )
    }
}
