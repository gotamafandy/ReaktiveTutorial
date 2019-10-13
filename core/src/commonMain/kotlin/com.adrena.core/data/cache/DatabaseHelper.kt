package com.adrena.core.data.cache

import com.adrena.core.sql.MoviesDatabase
import com.squareup.sqldelight.db.SqlDriver

expect fun getDriver(dbName: String): SqlDriver

class DatabaseHelper(
    dbName: String,
    sqlDriver: SqlDriver?) {

    val driver: SqlDriver = sqlDriver ?: getDriver(dbName)
    val database: MoviesDatabase = MoviesDatabase(driver)
}