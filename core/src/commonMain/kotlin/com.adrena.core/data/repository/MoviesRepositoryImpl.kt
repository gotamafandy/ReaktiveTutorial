package com.adrena.core.data.repository

import com.adrena.core.data.cache.Cache
import com.adrena.core.data.entity.Movie
import com.adrena.core.data.Service

class MoviesRepositoryImpl<R>(
    private val service: Service<R, List<Movie>>,
    private val cache: Cache<Movie>
): Repository<R, List<Movie>> {

    override suspend fun get(request: R?): List<Movie> {
        val cachedList = cache.selectAll()

        return if (cachedList.isNotEmpty()) {
            cachedList
        } else {
            val list = service.execute(request)

            cache.clear()
            cache.bulkInsert(list)

            list
        }
    }
}