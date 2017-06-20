package ch.ielse.demo.p05.api

import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast

import ch.ielse.demo.p05.Const
import ch.ielse.demo.p05.ObserverLifecycleHolder
import ch.ielse.demo.p05.R
import ch.ielse.demo.p05.view.ProgressLoadingDialog
import ch.ielse.demo.p05.view.Sneaker

import io.reactivex.Observer
import io.reactivex.annotations.NonNull
import io.reactivex.disposables.Disposable

abstract class SimpleObserver<E, T> : Observer<T> {
    private val delayHandler = Handler(Looper.getMainLooper())
    private var runProgress: Runnable? = null
    private var d: ProgressLoadingDialog? = null
    private var hasErrorToast = false
    private var sneaker: Sneaker? = null
    private var sneakerResult: String? = null
    private var disable: View? = null

    private var lifecycleHolder: ObserverLifecycleHolder? = null
    private var requestSuccess: Boolean? = null // real response result from server request

    fun attach(lifeHolder: ObserverLifecycleHolder): SimpleObserver<E, T> {
        lifecycleHolder = lifeHolder
        return this
    }

    fun showProgress(activity: Activity, message: String): SimpleObserver<E, T> {
        if (d == null) {
            runProgress = Runnable {
                if (!activity.isFinishing) {
                    d = ProgressLoadingDialog.Builder(activity).setTitle(message)
                            .setCancelable(false).create()
                    d!!.show()
                }
            }
            delayHandler.postDelayed(runProgress, 800L)
        }
        return this
    }

    fun sneaker(activity: Activity, message: String, result: String): SimpleObserver<E, T> {
        sneaker = Sneaker.with(activity).setMessage(message)
                .setLoading(true)
                .create()
        sneaker!!.show()
        sneakerResult = result
        return this
    }

    fun disable(view: View): SimpleObserver<E, T> {
        disable = view
        disable!!.isEnabled = false
        return this
    }

    internal fun errorToast(errorToast: Boolean): SimpleObserver<E, T> {
        hasErrorToast = errorToast
        return this
    }

    internal fun animateLoading(): SimpleObserver<E, T> {
        return this
    }

    override fun onSubscribe(@NonNull d: Disposable) {
        Const.logd("SimpleObserver onSubscribe " + d)
        lifecycleHolder!!.register(object : Disposable {
            override fun dispose() {
                detach(true)
                d.dispose()
            }

            override fun isDisposed(): Boolean {
                return d.isDisposed
            }
        })
    }

    @Suppress("UNCHECKED_CAST")
    override fun onNext(@NonNull t: T?) {
        Const.logd("SimpleObserver onNext " + t!!)
        if (t is ApiEntity) {
            val apiEntity = t
            if (apiEntity.err == ApiEntity.ERR_OK) {
                try {
                    val realResponse = apiEntity.extraReal()
                    if (realResponse != null) {
                        requestSuccess = true
                        onSuccess(realResponse as E)
                    } else
                        onError(NullPointerException("empty data -> realResponse == null"))
                } catch (parseError: Exception) {
                    onError(parseError)
                }
            } else if (apiEntity.err == ApiEntity.ERR_EXPIRED_SESSION) {
                Const.loge("SimpleObserver ERR_EXPIRED_SESSION")
            } else {
                Const.toast(apiEntity.errMsg, Toast.LENGTH_SHORT)
            }
        } else {
            // some 'special' api
            // onNext2(t)
        }
    }

//    protected fun onNext2(t: T) {
//    }

    protected abstract fun onSuccess(@NonNull o: E)

    override fun onError(@NonNull e: Throwable) {
        requestSuccess = false
        e.printStackTrace()
        Const.logd("SimpleObserver onError ")
        detach(false)
    }

    override fun onComplete() {
        Const.logd("SimpleObserver onComplete ")
        detach(false)
    }

    private fun detach(mute: Boolean) {
        if (!mute && hasErrorToast) {
            Const.toast(if (Const.hasNetwork()) "请求遇到了问题，请稍后再试" else "暂无网络环境", Toast.LENGTH_SHORT)
        }
        if (disable != null) {
            disable!!.isEnabled = true
        }
        if (runProgress != null) {
            delayHandler.removeCallbacks(runProgress)
            runProgress = null
        }
        if (d != null && d!!.isShowing) {
            try {
                d!!.dismiss()
            } catch (ignore: Exception) {
            } finally {
                d = null
            }
        }
        if (sneaker != null) {
            if (!mute && requestSuccess != null) {
                if (requestSuccess!!) {
                    sneaker!!.notifyStateChanged(R.mipmap.qrh, sneakerResult)
                } else {
                    sneaker!!.notifyStateChanged(R.mipmap.fdz,
                            if (Const.hasNetwork()) "请求遇到了问题，请稍后再试" else "暂无网络环境")
                }
            } else {
                sneaker!!.dismiss()
            }
            sneaker = null
        }
    }
}
