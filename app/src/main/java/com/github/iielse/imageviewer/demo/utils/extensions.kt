package com.github.iielse.imageviewer.demo.utils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.github.iielse.imageviewer.demo.core.LifecycleDisposable
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit

fun View.setOnClickCallback(callback: (View) -> Unit) {
    Observable.create<View> {
        setOnClickListener(it::onNext)
    }.throttleFirst(500, TimeUnit.MILLISECONDS)
            .doOnNext(callback)
            .subscribe().bindLifecycle(this)
}

fun Disposable.bindLifecycle(view: View) =
        this.apply {
            (view.activity as? AppCompatActivity)?.lifecycle?.addObserver(LifecycleDisposable(this))
        }

val View.activity: Activity?
    get() = getActivity(context)

// https://stackoverflow.com/questions/9273218/is-it-always-safe-to-cast-context-to-activity-within-view/45364110
private fun getActivity(context: Context?): Activity? {
    if (context == null) return null
    if (context is Activity) return context
    if (context is ContextWrapper) return getActivity(context.baseContext)
    return null
}