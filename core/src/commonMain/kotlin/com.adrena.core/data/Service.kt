package com.adrena.core.data.entity

interface Service<in R, T> {
    suspend fun execute(request: R?): T
}