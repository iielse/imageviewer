package com.github.iielse.imageviewer.demo.core

import androidx.lifecycle.Lifecycle
import com.github.iielse.imageviewer.demo.utils.bindLifecycle
import io.reactivex.observers.DisposableObserver

open class ObserverAdapter<T>(lifecycle: Lifecycle?) : DisposableObserver<T>() {
    init {
        bindLifecycle(lifecycle)
    }

    override fun onNext(t: T) {
    }

    override fun onError(e: Throwable) {
    }

    override fun onComplete() {
    }
}