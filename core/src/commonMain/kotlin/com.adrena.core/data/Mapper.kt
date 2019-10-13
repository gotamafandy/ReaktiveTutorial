package com.adrena.core.data

interface Mapper<in T, out E> {
    fun transform(response: T): E
}