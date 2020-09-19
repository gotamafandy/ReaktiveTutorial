package com.adrena.core.data

interface Service<in R, T> {
    suspend fun execute(request: R?): T
}