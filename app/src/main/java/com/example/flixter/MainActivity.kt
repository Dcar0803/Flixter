package com.example.flixter

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {

    private lateinit var movieAdapter: MovieAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        movieAdapter = MovieAdapter()
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = movieAdapter
        }

        fetchMovies()
    }

    private fun fetchMovies() {
        GlobalScope.launch(Dispatchers.IO) {
            val response = getMoviesFromApi()
            val movies = parseResponse(response)
            launch(Dispatchers.Main) {
                movieAdapter.setMovies(movies)
            }
        }
    }

    private fun getMoviesFromApi(): String {
        val apiKey = "a07e22bc18f5cb106bfe4cc1f83ad8ed"
        val url = URL("https://api.themoviedb.org/3/movie/now_playing?&api_key=$apiKey")
        val connection = url.openConnection() as HttpURLConnection
        try {
            val reader = BufferedReader(InputStreamReader(connection.inputStream))
            val response = StringBuilder()
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                response.append(line)
            }
            return response.toString()
        } finally {
            connection.disconnect()
        }
    }

    private fun parseResponse(response: String): List<Movie> {
        val gson = Gson()
        val movieResponse = gson.fromJson(response, MovieResponse::class.java)
        return movieResponse.results
    }
}
