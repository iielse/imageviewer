package com.github.iielse.imageviewer.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewConfiguration
import androidx.viewpager2.widget.ViewPager2
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.github.iielse.imageviewer.utils.Config
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class SubsamplingScaleImageView2 @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null)
    : SubsamplingScaleImageView(context, attrs) {
    interface Listener {
        fun onDrag(view: SubsamplingScaleImageView2, fraction: Float)
        fun onRestore(view: SubsamplingScaleImageView2, fraction: Float)
        fun onRelease(view: SubsamplingScaleImageView2)
    }

    private var initScale: Float? = null
    private val scaledTouchSlop by lazy { ViewConfiguration.get(context).scaledTouchSlop * Config.SWIPE_TOUCH_SLOP }
    private val dismissEdge by lazy { height * Config.DISMISS_FRACTION }
    private var singleTouch = true
    private var fakeDragOffset = 0f
    private var lastX = 0f
    private var lastY = 0f
    private var listener: Listener? = null

    init {
        setOnImageEventListener(object : DefaultOnImageEventListener() {
            override fun onImageLoaded() {
                initScale = null
            }
        })
    }

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
            MotionEvent.ACTION_DOWN -> if (initScale == null) initScale = scale
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> up()
            MotionEvent.ACTION_MOVE -> {
                if (singleTouch && scale == initScale) {
                    if (lastX == 0f) lastX = event.rawX
                    if (lastY == 0f) lastY = event.rawY
                    val offsetX = event.rawX - lastX
                    val offsetY = event.rawY - lastY
                    fakeDrag(offsetX, offsetY)
                }
            }
        }
    }

    private fun fakeDrag(offsetX: Float, offsetY: Float) {
        if (fakeDragOffset == 0f) {
            if (offsetY > scaledTouchSlop) fakeDragOffset = scaledTouchSlop
            else if (offsetY < -scaledTouchSlop) fakeDragOffset = -scaledTouchSlop
        }
        if (fakeDragOffset != 0f) {
            val fixedOffsetY = offsetY - fakeDragOffset
            parent?.requestDisallowInterceptTouchEvent(true)
            val fraction = abs(max(-1f, min(1f, fixedOffsetY / height)))
            val fakeScale = 1 - min(0.4f, fraction)
            scaleX = fakeScale
            scaleY = fakeScale
            translationY = fixedOffsetY
            translationX = offsetX / 2
            listener?.onDrag(this, fraction)
        }
    }

    private fun up() {
        parent?.requestDisallowInterceptTouchEvent(false)
        singleTouch = true
        fakeDragOffset = 0f
        lastX = 0f
        lastY = 0f

        if (abs(translationY) > dismissEdge) {
            listener?.onRelease(this)
        } else {
            val offsetY = translationY
            val fraction = min(1f, offsetY / height)
            listener?.onRestore(this, fraction)

            animate()
                    .translationX(0f).translationY(0f).scaleX(1f).scaleY(1f)
                    .setDuration(200).start()
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        animate().cancel()
    }
}
