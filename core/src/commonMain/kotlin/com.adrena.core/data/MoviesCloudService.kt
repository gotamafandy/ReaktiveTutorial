package com.adrena.core.data

import com.adrena.core.data.entity.Movie
import com.adrena.core.data.entity.MoviesResponse
import com.adrena.core.data.entity.Service
import io.ktor.client.HttpClient
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.features.logging.DEFAULT
import io.ktor.client.features.logging.LogLevel
import io.ktor.client.features.logging.Logger
import io.ktor.client.features.logging.Logging
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.response.HttpResponse
import io.ktor.client.response.readText
import io.ktor.http.HttpHeaders
import io.ktor.http.takeFrom
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json

class MoviesCloudService(
    private val key: String,
    private val hostUrl: String,
    private val mapper: Mapper<MoviesResponse, List<Movie>>): Service<String, List<Movie>> {

    @UseExperimental(UnstableDefault::class)
    override suspend fun execute(request: String?): List<Movie> {

        val httpResponse = client.get<HttpResponse> {
            apiUrl()
            parameter("s", request)
        }

        val json = httpResponse.readText()

        val response = Json.nonstrict.parse(MoviesResponse.serializer(), json)

        return mapper.transform(response)
    }

    private fun HttpRequestBuilder.apiUrl(path: String? = null) {
        header(HttpHeaders.CacheControl, "no-cache")
        url {
            takeFrom(hostUrl).parameters.append("apiKey", key)
            path?.let {
                encodedPath = it
            }
        }
    }

    private val client = HttpClient {
        install(JsonFeature) {
            serializer = KotlinxSerializer()
        }
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.ALL
        }
    }
}