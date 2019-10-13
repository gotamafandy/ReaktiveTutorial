package com.adrena.core.data

import com.adrena.core.data.entity.Movie
import com.adrena.core.data.entity.MoviesResponse

class MoviesMapper: Mapper<MoviesResponse, List<Movie>> {

    override fun transform(response: MoviesResponse): List<Movie> {
        return response.items.map {
            Movie(
                it.title,
                it.imdbID,
                it.year,
                it.type,
                it.poster
            )
        }
    }
}