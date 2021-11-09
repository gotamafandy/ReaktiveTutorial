package com.adrena.core.data

import io.ktor.client.HttpClient
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.features.logging.DEFAULT
import io.ktor.client.features.logging.LogLevel
import io.ktor.client.features.logging.Logger
import io.ktor.client.features.logging.Logging

actual val httpClient: HttpClient = HttpClient {
    expectSuccess = false

    install(JsonFeature) {
        serializer = KotlinxSerializer()
    }

    install(Logging) {
        logger = Logger.DEFAULT
        level = if (Platform.isDebugBinary) {
            LogLevel.ALL
        } else {
            LogLevel.NONE
        }
    }
}
