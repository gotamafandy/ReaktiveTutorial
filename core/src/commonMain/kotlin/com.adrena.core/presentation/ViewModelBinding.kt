package com.adrena.core.presentation

import com.badoo.reaktive.disposable.CompositeDisposable
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.subscribe

class ViewModelBinding {
    private val disposables = CompositeDisposable()

    fun <T> subscribe(observable: Observable<T>,
                      isThreadLocal: Boolean = true,
                      onSubscribe: ((Disposable) -> Unit)? = null,
                      onError: ((Throwable) -> Unit)? = null,
                      onComplete: (() -> Unit)? = null,
                      onNext: ((T) -> Unit)? = null) {

        disposables.add(observable.subscribe(isThreadLocal, onSubscribe, onError, onComplete, onNext))
    }

    fun <T> subscribe(observable: Observable<T>, onError: ((Throwable) -> Unit)? = null, onNext: ((T) -> Unit)? = null) {
        disposables.add(observable.subscribe(true, onError = onError, onNext = onNext))
    }

    fun <T> subscribe(observable: Observable<T>, onNext: ((T) -> Unit)? = null) {
        disposables.add(observable.subscribe(true, onNext = onNext))
    }

    fun dispose() {
        disposables.dispose()
    }
}