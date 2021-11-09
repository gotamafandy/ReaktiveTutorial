package com.adrena.core.data

import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*

actual val httpClient: HttpClient = HttpClient(Android) {
    expectSuccess = false

    install(JsonFeature) {
        serializer = KotlinxSerializer()
    }
    engine {
        connectTimeout = 100_000
        socketTimeout = 100_000
    }
    install(Logging) {
        logger = AndroidLogger()
        level = LogLevel.ALL
    }
}

class AndroidLogger: Logger {
    override fun log(message: String) {
        Napier.log(io.github.aakira.napier.LogLevel.DEBUG, "MOVIE", null, message)
    }
}