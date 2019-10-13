package com.adrena.core.domain

import com.adrena.core.data.entity.Movie
import com.adrena.core.data.repository.Repository

class UseCaseImpl<R, T>(
    private val repository: Repository<R, T>
): UseCase<R, T> {

    override suspend fun execute(request: R?): T {
        return repository.get(request)
    }
}