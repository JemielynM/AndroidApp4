package com.example.superpodcast

// Represents one podcast returned from the iTunes Search API
data class Podcast(
    val collectionId: Long,
    val collectionName: String,
    val artistName: String,
    val artworkUrl100: String?,
    val feedUrl: String?,
    val collectionViewUrl: String?,
    val trackCount: Int
)

// Represents the complete response returned by iTunes
data class PodcastSearchResponse(
    val resultCount: Int,
    val results: List<Podcast>
)