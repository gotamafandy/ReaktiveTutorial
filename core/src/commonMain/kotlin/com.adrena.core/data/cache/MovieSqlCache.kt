package com.adrena.core.data.cache

import com.adrena.core.data.entity.Movie

class MovieSqlCache(private val db: DatabaseHelper) : Cache<Movie> {
    override fun insert(model: Movie) {
        db.database.movieQueries.insert(
            model.title,
            model.imdbID,
            model.year,
            model.type,
            model.poster
        )
    }

    override fun bulkInsert(list: List<Movie>) {
        db.database.transaction {
            list.forEach {
                insert(it)
            }
        }
    }

    override fun clear() {
        db.database.movieQueries.deleteAll()
    }

    override fun selectAll(): List<Movie> {
        val records = db.database.movieQueries.selectAll().executeAsList()

        return records.map {
            Movie(it.title, it.imdbID, it.year, it.type, it.poster)
        }
    }
}