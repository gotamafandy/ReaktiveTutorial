package com.adrena.reaktive.tutorial

import com.adrena.core.data.Mapper
import com.adrena.core.data.entity.Movie
import com.adrena.reaktive.tutorial.model.MovieModel

class MovieModelsMapper: Mapper<List<Movie>, List<MovieModel>> {

    override fun transform(response: List<Movie>): List<MovieModel> {
        return response.map {
            MovieModel(
                it.title,
                it.imdbID,
                it.year,
                it.type,
                it.poster
            )
        }
    }
}