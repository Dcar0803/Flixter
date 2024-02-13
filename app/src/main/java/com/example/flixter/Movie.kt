package com.example.flixter

data class Movie(
    val title: String,
    val overview: String,
    val posterPath: String
)

data class MovieResponse(
    val results: List<Movie>
)
