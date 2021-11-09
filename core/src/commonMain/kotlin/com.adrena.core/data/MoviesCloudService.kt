package com.adrena.core.data

import com.adrena.core.data.entity.Movie
import com.adrena.core.data.entity.MoviesResponse
import io.ktor.client.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.util.*
import kotlinx.serialization.json.Json

val jsonConfiguration: Json by lazy {
    Json {
        isLenient = true
        ignoreUnknownKeys = true
    }
}

class MoviesCloudService(
    private val key: String,
    private val hostUrl: String,
    private val mapper: Mapper<MoviesResponse, List<Movie>>): Service<String, List<Movie>> {

    @OptIn(InternalAPI::class)
    override suspend fun execute(request: String?): List<Movie> {

        val httpResponse = httpClient.get<HttpStatement> {
            apiUrl()
            parameter("s", request)
        }

        val json = httpResponse.execute().readText()

        val response = jsonConfiguration.decodeFromString(MoviesResponse.serializer(), json)

        return mapper.transform(response)
    }

    @OptIn(InternalAPI::class)
    private fun HttpRequestBuilder.apiUrl(path: String? = null) {
        header(HttpHeaders.CacheControl, "no-cache")
        url {
             takeFrom(hostUrl).parameters.append("apiKey", key)
            path?.let {
                encodedPath = it
            }
        }
    }
}