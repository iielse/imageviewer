package github.iielse.imageviewer.demo

import android.os.Bundle
import android.util.SparseArray
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.github.iielse.imageviewer.ImageViewerBuilder
import com.github.iielse.imageviewer.`interface`.DataProviderAdapter
import com.github.iielse.imageviewer.model.Photo

class MainActivity6 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ImageViewerBuilder(this)
                .setDataProvider(SimpleDataProvider())
                .show()
    }


}

class SimpleDataProvider : DataProviderAdapter() {
    override fun getInitial(): SparseArray<Pair<ImageView, Photo>> {
        return SparseArray()
    }
}