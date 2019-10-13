package com.adrena.reaktive.tutorial.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MovieModel(
    val title: String,
    val imdbID: String,
    val year: String,
    val type: String,
    val poster: String
): Parcelable