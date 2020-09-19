package com.adrena.core.data.cache

import com.adrena.core.sql.MoviesDatabase
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.drivers.native.NativeSqliteDriver

actual fun getDriver(dbName: String): SqlDriver {
    return NativeSqliteDriver(MoviesDatabase.Schema, dbName)
}
