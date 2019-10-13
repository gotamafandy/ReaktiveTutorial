package com.adrena.core.data.repository

interface Repository<in R, T> {
    suspend fun get(request: R?): T
}