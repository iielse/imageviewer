package com.github.iielse.imageviewer.demo.core

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import io.reactivex.disposables.Disposable

class LifecycleDisposable(private val disposable: Disposable) : LifecycleObserver {
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop() {
        disposable.dispose()
    }
}