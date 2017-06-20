package ch.ielse.demo.p05

import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


open class BaseActivity : AppCompatActivity(), ObserverLifecycleHolder {
    internal val apiRequesting = CompositeDisposable()

    protected var isLandscapeMode = false
    protected var isTranslucentStatus = false
    protected var isTransparentBackground = true

    override fun onCreate(savedInstanceState: Bundle?) {
        if (isTransparentBackground) {
            window.setBackgroundDrawableResource(android.R.color.transparent)
        }
        if (isTranslucentStatus && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        }
        requestedOrientation = if (isLandscapeMode) ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE else ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        super.onCreate(savedInstanceState)
    }


    override fun register(d: Disposable) {
        apiRequesting.add(d)
    }

    override fun unregisterAll() {
        apiRequesting.clear()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun handleBusEvent(event: BusEvent) {
        when (event.act) {
            BusEvent.FINISH -> if (event.obj == javaClass.simpleName) finish()
            BusEvent.FINISH_EXCEPT -> if (event.obj != javaClass.simpleName) finish()
            BusEvent.FINISH_ALL -> finish()
            else -> handleBusEventImpl(event)
        }
    }

    protected open fun handleBusEventImpl(event: BusEvent) {
        Const.logd("handleBusEventImpl " + event)
    }

    override fun onDestroy() {
        super.onDestroy()
        hideSoftKeyboard()
    }

}
