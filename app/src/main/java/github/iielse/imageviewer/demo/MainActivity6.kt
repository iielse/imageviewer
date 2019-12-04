package github.iielse.imageviewer.demo

import android.os.Bundle
import android.util.SparseArray
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import ch.ielse.demo.p02.Data
import ch.ielse.demo.p02.R
import com.bumptech.glide.Glide
import com.github.iielse.imageviewer.ImageViewerBuilder
import com.github.iielse.imageviewer.`interface`.DataProviderAdapter
import com.github.iielse.imageviewer.model.Photo
import kotlinx.android.synthetic.main.activity_9.*

class MainActivity6 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_9)

        Glide.with(pView)
                .load("https://img-blog.csdnimg.cn/20190505120041887.gif")
                .into(pView)

        pView.setOnClickListener {
            ImageViewerBuilder(this)
                    .setDataProvider(SimpleDataProvider())
                    .show()
        }
    }
}

class SimpleDataProvider : DataProviderAdapter() {
    override fun getInitial(): SparseArray<Pair<ImageView, Photo>> {
        return SparseArray()
    }
}