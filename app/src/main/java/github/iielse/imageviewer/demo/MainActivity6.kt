package github.iielse.imageviewer.demo

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import ch.ielse.demo.p02.R
import com.bumptech.glide.Glide
import com.github.iielse.imageviewer.ImageViewerBuilder
import com.github.iielse.imageviewer.DataProviderAdapter
import com.github.iielse.imageviewer.Transform
import com.github.iielse.imageviewer.Photo
import kotlinx.android.synthetic.main.activity_9.*

class MainActivity6 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_9)

        Glide.with(pView)
//                .load("https://img-blog.csdnimg.cn/20190505120041887.gif")
                .load("http://static.unicolive.com/20190715/me_0-1563200961.618469")
                .into(pView)

        pView.setOnClickListener {
            ImageViewerBuilder(this)
                    .setDataProvider(object : DataProviderAdapter() {

                        var curr = 0
                        override fun getInitial(): List<Photo> {
                            return listOf(Photo(id = "1", url = "http://static.unicolive.com/20190715/me_0-1563200961.618469", height = 0, width = 0))
                        }

                        override fun getMore(callback: (List<Photo>) -> Unit) {
                            fetch(curr) {
                                 callback(it.map { Photo(id= it.id.toString(), url = it.imageUrl , width = 0, height = 0) })
                                curr++
                            }
                        }
                    })
                    .setTransform(object : Transform {
                        override fun getOriginView(pos: Int): ImageView? {
                            return when (pos) {
                                0 -> pView
                                else -> null
                            }
                        }
                    })
                    .setInitialPosition(1)
                    .show()
        }
    }
}
