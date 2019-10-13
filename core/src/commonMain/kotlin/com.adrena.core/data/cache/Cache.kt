package com.adrena.core.data.cache

interface Cache<T> {
    fun insert(model: T)
    fun bulkInsert(list: List<T>)
    fun clear()
    fun selectAll(): List<T>
}