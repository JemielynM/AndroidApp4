package com.example.superpodcast

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Creates the Retrofit connection to the iTunes Search API
object RetrofitInstance {

    private const val BASE_URL = "https://itunes.apple.com/"

    val api: ITunesApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ITunesApi::class.java)
    }
}
