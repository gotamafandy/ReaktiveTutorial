package com.adrena.core.presentation

import com.adrena.core.data.Mapper
import com.adrena.core.data.entity.Movie
import com.adrena.core.domain.UseCase
import com.badoo.reaktive.coroutinesinterop.singleFromCoroutine
import com.badoo.reaktive.observable.*
import com.badoo.reaktive.subject.publish.PublishSubject

class ListViewModelImpl<R, E>(
    useCase: UseCase<R, List<Movie>>,
    mapper: Mapper<List<Movie>, List<E>>?
): ListViewModel<R, E>, ListViewModelInput<R>, ListViewModelOutput<R, E> {
    override val inputs: ListViewModelInput<R> = this
    override val outputs: ListViewModelOutput<R, E> = this

    override val loading: Observable<Boolean>
    override val result: Observable<List<E>>

    private val mListProperty = PublishSubject<R>()
    private val mLoadMoreProperty = PublishSubject<R>()

    init {
        val loadingProperty = PublishSubject<Boolean>()

        val items = mutableListOf<E>()

        var clearItems = false

        loading = loadingProperty

        val initialRequest = mListProperty
            .doOnBeforeNext { loadingProperty.onNext(true) }
            .flatMapSingle { request ->
                singleFromCoroutine { useCase.execute(request) }
            }
            .doOnBeforeNext {
                loadingProperty.onNext(false)
                clearItems = true
            }

        val nextRequest = mLoadMoreProperty
            .doOnBeforeNext { loadingProperty.onNext(true) }
            .flatMapSingle { request ->
                singleFromCoroutine { useCase.execute(request) }
            }
            .doOnBeforeNext {
                loadingProperty.onNext(false)
                clearItems = false
            }

        result = merge(initialRequest, nextRequest).map {
            if (clearItems) {
                items.clear()
            }

            @Suppress("UNCHECKED_CAST")
            val list = mapper?.transform(it) ?: it as List<E>

            items.addAll(list)

            items
        }

    }

    override fun get(request: R) {
        mListProperty.onNext(request)
    }

    override fun loadMore(request: R) {
        mLoadMoreProperty.onNext(request)
    }

}