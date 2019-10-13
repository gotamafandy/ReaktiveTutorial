package com.adrena.core.domain

interface UseCase<in R, T> {
    suspend fun execute(request: R?): T
}