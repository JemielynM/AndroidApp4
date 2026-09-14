package com.example.superpodcast

import retrofit2.http.GET
import retrofit2.http.Query

// Defines the network request used to search for podcasts on iTunes
interface ITunesApi {

    @GET("search")
    suspend fun searchPodcasts(
        @Query("term") searchTerm: String,
        @Query("media") media: String = "podcast",
        @Query("entity") entity: String = "podcast",
        @Query("limit") limit: Int = 20
    ): PodcastSearchResponse
}
