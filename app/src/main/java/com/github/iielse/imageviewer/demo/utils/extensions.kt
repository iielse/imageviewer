package com.github.iielse.imageviewer.demo.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Color
import android.os.Build
import android.util.LruCache
import android.view.LayoutInflater
import android.view.View
import android.view.View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
import android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.*
import com.github.iielse.imageviewer.demo.R
import io.reactivex.disposables.Disposable
import androidx.core.graphics.toColorInt

fun View.setOnClickCallback(interval: Long = 500L, callback: (View) -> Unit) {
    if (!isClickable) isClickable = true
    if (!isFocusable) isFocusable = true
    setOnClickListener(object : View.OnClickListener {
        override fun onClick(v: View?) {
            v ?: return
            val lastClickedTimestamp = v.getTag(R.id.view_last_click_timestamp)?.toString()?.toLongOrNull() ?: 0L
            val currTimestamp = System.currentTimeMillis()
            if (currTimestamp - lastClickedTimestamp < interval) return
            v.setTag(R.id.view_last_click_timestamp, currTimestamp)
            callback(v)
        }
    })
}

fun Disposable.bindLifecycle(lifecycle: Lifecycle?) {
    lifecycle?.observeOnDestroy { dispose() }
}

val View.activity: FragmentActivity?
    get() = getActivity(context) as FragmentActivity?

// https://stackoverflow.com/questions/9273218/is-it-always-safe-to-cast-context-to-activity-within-view/45364110
private fun getActivity(context: Context?): Activity? {
    if (context == null) return null
    if (context is Activity) return context
    if (context is ContextWrapper) return getActivity(context.baseContext)
    return null
}

//val View.lifecycleOwner: LifecycleOwner? get() {
//    val activity = activity as? FragmentActivity? ?: return null
//    val fragment = findSupportFragment(this, activity)
//    return fragment?.viewLifecycleOwner ?: activity
//}
//private val tempViewToSupportFragment = ArrayMap<View, Fragment>()
//private fun findSupportFragment(target: View, activity: FragmentActivity): Fragment? {
//    tempViewToSupportFragment.clear()
//    findAllSupportFragmentsWithViews(
//            activity.supportFragmentManager.fragments, tempViewToSupportFragment
//    )
//    var result: Fragment? = null
//    val activityRoot = activity.findViewById<View>(android.R.id.content)
//    var current = target
//    while (current != activityRoot) {
//        result = tempViewToSupportFragment[current]
//        if (result != null) {
//            break
//        }
//        current = if (current.parent is View) {
//            current.parent as View
//        } else {
//            break
//        }
//    }
//    tempViewToSupportFragment.clear()
//    return result
//}
//
//private fun findAllSupportFragmentsWithViews(
//        topLevelFragments: Collection<Fragment?>?, result: MutableMap<View?, Fragment>
//) {
//    if (topLevelFragments == null) {
//        return
//    }
//    for (fragment in topLevelFragments) {
//        // getFragment()s in the support FragmentManager may contain null values, see #1991.
//        if (fragment?.view == null) {
//            continue
//        }
//        result[fragment.view] = fragment
//        findAllSupportFragmentsWithViews(fragment.childFragmentManager.fragments, result)
//    }
//}

val View.lifecycleOwner: LifecycleOwner get() {
    val self = this
    var owner = self.getTag(R.id.view_lifecycle_owner) as? LifecycleOwner?
    if (owner == null) {
        val lifecycleOwner = object : LifecycleOwner {
            private val registry = LifecycleRegistry(this)
            override val lifecycle = registry
        }
        self.setTag(R.id.view_lifecycle_owner, lifecycleOwner)
        val viewLifecycle = lifecycleOwner.lifecycle
        owner = lifecycleOwner
        self.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View) {
                viewLifecycle.currentState = Lifecycle.State.CREATED
                viewLifecycle.handleLifecycleEvent(Lifecycle.Event.ON_START)
                viewLifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
            }

            override fun onViewDetachedFromWindow(v: View) {
                viewLifecycle.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
                viewLifecycle.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
                viewLifecycle.currentState = Lifecycle.State.DESTROYED
                self.setTag(R.id.view_lifecycle_owner, null)
                self.removeOnAttachStateChangeListener(this)
            }
        })
    }
    return owner
}

fun <T> LiveData<T>.observe(view: View, observer: Observer<T>) {
    this.observe(view.lifecycleOwner, observer)
}

fun Lifecycle.observeOnDestroy(block: () -> Unit) {
    val self = this
    self.addObserver(object : LifecycleEventObserver {
        override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
            if (event == Lifecycle.Event.ON_DESTROY) {
                block()
                self.removeObserver(this)
            }
        }
    })
}

fun Lifecycle.observeOnResume(once: Boolean = true, block: () -> Unit) {
    val self = this
    self.addObserver(object : LifecycleEventObserver {
        override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
            if (event == Lifecycle.Event.ON_RESUME) {
                block()
                if (once) self.removeObserver(this)
            } else if (event == Lifecycle.Event.ON_DESTROY) {
                self.removeObserver(this)
            }
        }
    })
}


fun ViewGroup.inflate(resId: Int): View {
    return LayoutInflater.from(context).inflate(resId, this, false)
}

fun Activity.useStatusBar(
    statusBarLight: Boolean = true,
    navigationBarLight: Boolean = true,
    statusBarTranslucent: Boolean = true,
    navigationBarTranslucent: Boolean = false,
) {
    window?.useStatusBar(statusBarLight,navigationBarLight,statusBarTranslucent,navigationBarTranslucent)
}
fun Window?.useStatusBar(
    statusBarLight: Boolean,
    navigationBarLight: Boolean,
    statusBarTranslucent: Boolean,
    navigationBarTranslucent: Boolean,
) {
    if (this == null) return
    fun setWindowFlag(
        win: Window,
        bits: Int,
        on: Boolean
    ) {
        val winParams = win.attributes
        if (on) {
            winParams.flags = winParams.flags or bits
        } else {
            winParams.flags = winParams.flags and bits.inv()
        }
        win.attributes = winParams
    }

    if (statusBarTranslucent) {
        setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false)
    }
    statusBarColor = Color.TRANSPARENT

    if (navigationBarTranslucent) {
        setWindowFlag(
            this,
            WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
            false
        )
    }
    navigationBarColor = Color.TRANSPARENT
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        navigationBarDividerColor = Color.TRANSPARENT
    }
    var result = View.SYSTEM_UI_FLAG_VISIBLE
    if (statusBarTranslucent) result =
        result or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    if (navigationBarTranslucent) result =
        result or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
    if (statusBarLight) if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        result = result or SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        if (navigationBarLight) result =
            result or SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
    }
    try {
        decorView.systemUiVisibility = result
    } catch (ignored: Throwable) {}
}

val String.colorInt: Int get() = getColor(this)
private val colors = LruCache<String, Int>(80)
private fun getColor(color: String): Int {
    val result = colors[color]
    if (result != null) return result
    return try {
        color.toColorInt().also { colors.put(color, it) }
    } catch (e: Exception) {
        e.printStackTrace()
        Color.TRANSPARENT
    }
}
