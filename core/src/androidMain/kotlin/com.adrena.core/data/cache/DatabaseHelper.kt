package com.adrena.core.data.cache

import com.squareup.sqldelight.db.SqlDriver

actual fun getDriver(dbName: String): SqlDriver = throw UninitializedPropertyAccessException("Android SqlDriver must be provided from main app")
