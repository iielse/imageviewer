package github.iielse.imageviewer.demo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.LongSparseArray
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import ch.ielse.demo.p02.R
import ch.ielse.demo.p02.TestActivity
import com.bumptech.glide.Glide
import com.github.iielse.imageviewer.ImageViewerBuilder
import com.github.iielse.imageviewer.utils.Config
import kotlinx.android.synthetic.main.activity_9.*

class MainActivity6 : AppCompatActivity() {
    private val myCustomController by lazy { MyCustomController(this) }
    private val myData0 = MyData(id = 0, url = "")
    private val myData9 = MyData(id = 9, url = "https://www.google.cn/landing/cnexp/google-search.png")
    private val myData21 = MyData(id = 21, url = "")
    private val myData4 = MyData(id = 4 , url = "http://pic.3h3.com/up/2014-3/20143314140858312456.gif")
    private val transformerMapping by lazy {
        LongSparseArray<ImageView>().apply {
            put(myData21.id, pView1)
            put(myData0.id, pView2)
            put(myData9.id, pView3)
            put(myData4.id, pView4)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        X.appContext = this.applicationContext
        setContentView(R.layout.activity_9)
        toTest.setOnClickListener { startActivity(Intent(this, TestActivity::class.java)) }
        orientation.setOnClickListener {
            var orientationH = it.tag as? Boolean? ?: true
            orientationH = !orientationH
            orientation.text = if (orientationH) " HORIZONTAL" else "VERTICAL"
            Config.VIEWER_ORIENTATION = if (orientationH) ViewPager2.ORIENTATION_HORIZONTAL else ViewPager2.ORIENTATION_VERTICAL
            it.tag = orientationH
        }

        Glide.with(pView1).load(provideBitmap(myData21.id)).into(pView1)
        Glide.with(pView2).load(provideBitmap(myData0.id)).into(pView2)
        Glide.with(pView3).load(myData9.url).into(pView3)
        Glide.with(pView4).load(myData4.url).into(pView4)
        pView1.setOnClickListener { builder(myData21).show() }
        pView2.setOnClickListener { builder(myData0).show() }
        pView3.setOnClickListener { builder(myData9).show() }
        pView4.setOnClickListener { builder(myData4).show() }
    }

    private fun builder(clickedData: MyData): ImageViewerBuilder {
        return ImageViewerBuilder(
                context = this,
                initKey = clickedData.id,
                dataProvider = MyTestDataProvider(clickedData),
                imageLoader = MySimpleLoader(),
                transformer = MyTransformer(transformerMapping)
        ).also { myCustomController.init(it) }
    }
}
