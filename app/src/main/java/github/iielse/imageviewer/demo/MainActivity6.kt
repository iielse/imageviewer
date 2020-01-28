package github.iielse.imageviewer.demo

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import ch.ielse.demo.p02.R
import ch.ielse.demo.p02.TestActivity
import com.bumptech.glide.Glide
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.github.iielse.imageviewer.ImageViewerActionViewModel
import com.github.iielse.imageviewer.ImageViewerBuilder
import com.github.iielse.imageviewer.core.*
import com.github.iielse.imageviewer.utils.Config
import com.github.iielse.imageviewer.utils.inflate
import com.github.iielse.imageviewer.utils.log
import kotlinx.android.synthetic.main.activity_9.*
import java.io.File

class MainActivity6 : AppCompatActivity() {
    private val viewerActions by lazy { ViewModelProvider(this).get(ImageViewerActionViewModel::class.java) }

    private val init101 = MyViewerData(id = 101, url = "")
    private val initX = MyViewerData(id = 0, url = "")
    private var indicatorDecor: View? = null
    private var overlayIndicator: TextView? = null
    private var preIndicator: TextView? = null
    private var nextIndicator: TextView? = null
    private var orientationH = true
    private var currentPosition = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_9)
        toTest.setOnClickListener { startActivity(Intent(this, TestActivity::class.java)) }
        orientation.setOnClickListener {
            orientationH = !orientationH
            orientation.text = if (orientationH) " HORIZONTAL" else "VERTICAL"
            Config.VIEWER_ORIENTATION = if (orientationH) ViewPager2.ORIENTATION_HORIZONTAL else ViewPager2.ORIENTATION_VERTICAL
        }


        Glide.with(pView)
                .load(provideBitmap(init101.id))
                .into(pView)
        Glide.with(pViewx)
                .load(provideBitmap(initX.id))
                .into(pViewx)

        pView.setOnClickListener {
            builder(init101, pView).show()
        }
        pViewx.setOnClickListener {
            builder(initX, pViewx).show()
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
                    override fun onRelease(viewHolder: RecyclerView.ViewHolder, view: View) {
                        viewHolder.itemView.findViewById<View>(R.id.customizeDecor)
                                .animate().setDuration(200).alpha(0f).start()

                        indicatorDecor?.animate()?.setDuration(200)?.alpha(0f)?.start()
                    }

                    override fun onPageSelected(position: Int) {
                        currentPosition = position
                        overlayIndicator?.text = position.toString()
                    }
                })
                .setOverlayCustomizer(object : OverlayCustomizer {
                    override fun provideView(parent: ViewGroup): View? {
                        return parent.inflate(R.layout.layout_indicator).also {
                            indicatorDecor = it.findViewById(R.id.indicatorDecor)
                            overlayIndicator = it.findViewById(R.id.indicator)
                            preIndicator = it.findViewById(R.id.pre)
                            nextIndicator = it.findViewById(R.id.next)

                            preIndicator?.setOnClickListener {
                                viewerActions.setCurrentItem(currentPosition - 1)
                            }
                            nextIndicator?.setOnClickListener {
                                viewerActions.setCurrentItem(currentPosition + 1)
                            }
                        }
                    }
                })
    }
}