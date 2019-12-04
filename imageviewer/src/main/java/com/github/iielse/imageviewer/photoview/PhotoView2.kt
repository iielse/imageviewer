package com.github.iielse.imageviewer.photoview

import android.content.Context
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import com.github.chrisbanes.photoview.OnMatrixChangedListener
import com.github.chrisbanes.photoview.PhotoView
import com.github.iielse.imageviewer.core.decimal2
import kotlin.math.min

class PhotoView2 @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : PhotoView(context, attrs, defStyleAttr), OnMatrixChangedListener {
    companion object {
        private const val EDGE_NONE = -1
        private const val EDGE_TOP = 3
    }

    private val dismissEdge by lazy { height }
    private var scrollEdge = EDGE_NONE
    private var lastRect: RectF? = null
    private var singleTouch = true
    private var lastX = 0f
    private var lastY = 0f

    init {
        setOnMatrixChangeListener(this)
    }

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        handleDispatchTouchEvent(event)
        return super.dispatchTouchEvent(event)
    }

    override fun onMatrixChanged(rect: RectF?) {
        val last = lastRect
        scrollEdge = if (rect != null && last != null && rect.top.decimal2 >= last.top.decimal2) EDGE_TOP else EDGE_NONE
        lastRect = rect
    }

    private fun handleDispatchTouchEvent(event: MotionEvent?) {
        when (event?.actionMasked) {
            MotionEvent.ACTION_POINTER_DOWN -> {
                singleTouch = false
                animate().translationY(0f).setDuration(200).start()
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> reset()
            MotionEvent.ACTION_MOVE -> {
                if (singleTouch && scrollEdge == EDGE_TOP) {
                    if (lastX == 0f) lastX = event.rawX
                    if (lastY == 0f) lastY = event.rawY
                    val offsetX = event.rawX - lastX
                    val offsetY = event.rawY - lastY
                    if (offsetY > 0) transform(offsetX, offsetY)
                }
            }
        }
    }

    private fun transform(offsetX: Float, offsetY: Float) {
        setAllowParentInterceptOnEdge(false)
        translationY = offsetY
        translationX = offsetX / 2
        val fakeScale = 1 - min(0.3f, offsetY / dismissEdge)
        scaleX = fakeScale
        scaleY = fakeScale
    }

    private fun reset() {
        setAllowParentInterceptOnEdge(true)
        singleTouch = true
        lastY = 0f

        if (translationY > dismissEdge) {

        } else {
            animate().translationY(0f).scaleX(1f).scaleY(1f).setDuration(200).start()
        }
    }
}