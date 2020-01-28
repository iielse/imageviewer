package com.github.iielse.imageviewer.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager2.widget.ViewPager2
import com.github.chrisbanes.photoview.PhotoView
import com.github.iielse.imageviewer.utils.Config
import kotlin.math.min

class PhotoView2 @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : PhotoView(context, attrs, defStyleAttr) {
    interface Listener {
        fun onDrag(view: PhotoView2, fraction: Float)
        fun onRestore(view: PhotoView2, fraction: Float)
        fun onRelease(view: PhotoView2)
    }
    private val dismissEdge by lazy { height * Config.DISMISS_FRACTION }
    private var singleTouch = true
    private var lastX = 0f
    private var lastY = 0f
    private var listener: Listener? = null

    fun setListener(listener: Listener?) {
        this.listener = listener
    }

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        if (Config.SWIPE_DISMISS && Config.VIEWER_ORIENTATION == ViewPager2.ORIENTATION_HORIZONTAL) {
            handleDispatchTouchEvent(event)
        }
        return super.dispatchTouchEvent(event)
    }

    private fun handleDispatchTouchEvent(event: MotionEvent?) {
        when (event?.actionMasked) {
            MotionEvent.ACTION_POINTER_DOWN -> {
                singleTouch = false
                animate()
                        .translationX(0f).translationY(0f).scaleX(1f).scaleY(1f)
                        .setDuration(200).start()
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> up()
            MotionEvent.ACTION_MOVE -> {
                if (singleTouch && scale == 1f) {
                    if (lastX == 0f) lastX = event.rawX
                    if (lastY == 0f) lastY = event.rawY
                    val offsetX = event.rawX - lastX
                    val offsetY = event.rawY - lastY
                    if (offsetY > 0) fakeDrag(offsetX, offsetY)
                }
            }
        }
    }

    private fun fakeDrag(offsetX: Float, offsetY: Float) {
        setAllowParentInterceptOnEdge(false)
        val fraction = min(1f, offsetY / height)
        val fakeScale = 1 - min(0.4f, fraction)
        scaleX = fakeScale
        scaleY = fakeScale
        translationY = offsetY
        translationX = offsetX / 2
        listener?.onDrag(this, fraction)
    }

    private fun up() {
        setAllowParentInterceptOnEdge(true)
        singleTouch = true
        lastX = 0f
        lastY = 0f

        if (translationY > dismissEdge) {
            listener?.onRelease(this)
        } else {
            animate()
                    .translationX(0f).translationY(0f).scaleX(1f).scaleY(1f)
                    .setDuration(200)
                    .setUpdateListener {
                        val offsetY = translationY
                        val fraction = min(1f, offsetY / dismissEdge)
                        listener?.onRestore(this, fraction)
                    }
                    .start()
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        animate().cancel()
    }
}