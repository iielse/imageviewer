package com.github.iielse.imageviewer.widgets.video

import android.content.Context
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import androidx.viewpager2.widget.ViewPager2
import com.github.iielse.imageviewer.utils.Config
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class ExoVideoView2 @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : ExoVideoView(context, attrs, defStyleAttr), View.OnTouchListener {

    interface Listener {
        fun onDrag(view: ExoVideoView2, fraction: Float)
        fun onRestore(view: ExoVideoView2, fraction: Float)
        fun onRelease(view: ExoVideoView2)
    }

    private val scaledTouchSlop by lazy { ViewConfiguration.get(context).scaledTouchSlop * Config.SWIPE_TOUCH_SLOP }
    private val dismissEdge by lazy { height * Config.DISMISS_FRACTION }
    private var singleTouch = true
    private var fakeDragOffset = 0f
    private var lastX = 0f
    private var lastY = 0f
    private val listeners = mutableListOf<Listener>()
    private var clickListener: OnClickListener? = null
    private var longClickListener: OnLongClickListener? = null

    init {
        setOnTouchListener(this)
    }

    fun addListener(listener: Listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener)
        }
    }

    override fun setOnClickListener(listener: OnClickListener?) {
        clickListener = listener
    }

    override fun setOnLongClickListener(listener: OnLongClickListener?) {
        longClickListener = listener
    }

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        if (Config.SWIPE_DISMISS && Config.VIEWER_ORIENTATION == ViewPager2.ORIENTATION_HORIZONTAL) {
            handleDispatchTouchEvent(event)
        }
        return super.dispatchTouchEvent(event)
    }

    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        gestureDetector.onTouchEvent(event)
        return true
    }

    private fun handleDispatchTouchEvent(event: MotionEvent?) {
        if (!prepared) return

        when (event?.actionMasked) {
            MotionEvent.ACTION_POINTER_DOWN -> {
                singleTouch = false
                animate()
                        .translationX(0f).translationY(0f).scaleX(1f).scaleY(1f)
                        .setDuration(200).start()
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> up()
            MotionEvent.ACTION_MOVE -> {
                if (singleTouch) {
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
            listeners.toList().forEach { it.onDrag(this, fraction) }
        }
    }

    private fun up() {
        parent?.requestDisallowInterceptTouchEvent(false)
        singleTouch = true
        fakeDragOffset = 0f
        lastX = 0f
        lastY = 0f

        if (abs(translationY) > dismissEdge) {
            listeners.toList().forEach { it.onRelease(this) }
        } else {
            val offsetY = translationY
            val fraction = min(1f, offsetY / height)
            listeners.toList().forEach { it.onRestore(this, fraction) }

            animate()
                    .translationX(0f).translationY(0f).scaleX(1f).scaleY(1f)
                    .setDuration(200).start()
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        animate().cancel()
    }

    private val gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
        // forward long click listener
        override fun onLongPress(e: MotionEvent) {
            longClickListener?.onLongClick(this@ExoVideoView2)
        }
    }).apply {
        setOnDoubleTapListener(object : GestureDetector.OnDoubleTapListener {
            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                clickListener?.onClick(this@ExoVideoView2)
                return true
            }

            override fun onDoubleTapEvent(e: MotionEvent) = false
            override fun onDoubleTap(e: MotionEvent): Boolean = true
        })
    }
}