package ch.ielse.demo.p05

import io.reactivex.disposables.Disposable

interface ObserverLifecycleHolder {

    fun register(d: Disposable)

    fun unregisterAll()
}
