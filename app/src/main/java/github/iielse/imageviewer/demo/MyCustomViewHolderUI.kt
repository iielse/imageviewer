package github.iielse.imageviewer.demo

import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ch.ielse.demo.p02.R
import com.github.iielse.imageviewer.core.Photo
import com.github.iielse.imageviewer.core.VHCustomizer
import com.github.iielse.imageviewer.utils.inflate

class MyCustomViewHolderUI : VHCustomizer {
    override fun initialize(type: Int, viewHolder: RecyclerView.ViewHolder) {
        (viewHolder.itemView as? ViewGroup?)?.let {
            it.addView(it.inflate(R.layout.item_photo_custom_layout))
        }
    }

    override fun bind(type: Int, data: Photo, viewHolder: RecyclerView.ViewHolder) {
        (viewHolder.itemView as? ViewGroup?)?.let {
            val x = data as MyData
            it.findViewById<TextView>(R.id.exText).text = x.desc
        }
    }
}