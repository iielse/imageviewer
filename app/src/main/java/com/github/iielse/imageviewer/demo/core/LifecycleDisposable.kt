package com.github.iielse.imageviewer.demo.core

import androidx.lifecycle.*
import io.reactivex.disposables.Disposable

class LifecycleDisposable(
        private val lifecycle: Lifecycle,
        private val disposable: Disposable) : LifecycleEventObserver {
    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        if (event == Lifecycle.Event.ON_DESTROY) {
            disposable.dispose()
            lifecycle.removeObserver(this)
        }
    }
}