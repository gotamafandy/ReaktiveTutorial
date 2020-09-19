package com.adrena.core.data

import com.adrena.core.data.entity.Movie
import com.adrena.core.data.entity.MoviesResponse
import io.ktor.client.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.Json

class MoviesCloudService(
    private val key: String,
    private val hostUrl: String,
    private val mapper: Mapper<MoviesResponse, List<Movie>>): Service<String, List<Movie>> {

    override suspend fun execute(request: String?): List<Movie> {

        val httpResponse = client.get<HttpStatement> {
            apiUrl()
            parameter("s", request)
        }

        val json = httpResponse.execute().readText()

        val response = Json {
            isLenient = true
            ignoreUnknownKeys = true
        }.decodeFromString(MoviesResponse.serializer(), json)

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

        /**
         * Remove logging as the latest ktor release has changed
         * coroutines pattern usage and AwaitAll bug,
         * Activating logging will cause crash on K/N awaitAll
         */
        /*
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.ALL
        }
        */
    }
}