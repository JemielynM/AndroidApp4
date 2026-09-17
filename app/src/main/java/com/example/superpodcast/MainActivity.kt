package com.example.superpodcast

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.superpodcast.ui.theme.SuperPodcastTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SuperPodcastTheme {
                SuperPodcastApp()
            }
        }
    }
}

@Composable
fun SuperPodcastApp() {

    // Stores the text typed by the user
    var searchText by remember { mutableStateOf("") }

    // Stores the podcast results returned by iTunes
    var podcasts by remember { mutableStateOf<List<Podcast>>(emptyList()) }

    // Shows whether a network request is currently running
    var isLoading by remember { mutableStateOf(false) }

    // Displays an error if something goes wrong
    var errorMessage by remember { mutableStateOf("") }

    // Tracks whether the user has completed a search
    var hasSearched by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(30.dp))

        Text(
            text = "🎙️ SuperPodcast",
            fontSize = 30.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Search iTunes Podcasts",
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            label = { Text("Podcast search") },
            placeholder = { Text("Example: technology") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {

                if (searchText.isNotBlank()) {

                    coroutineScope.launch {

                        isLoading = true
                        errorMessage = ""
                        hasSearched = false

                        try {

                            // Sends the search request to the iTunes API
                            val response =
                                RetrofitInstance.api.searchPodcasts(searchText)

                            // Unusual criteria:
                            // only show podcasts with at least 3 words in the title
                            podcasts = response.results.filter {
                                it.collectionName.trim()
                                    .split(Regex("\\s+"))
                                    .size >= 3
                            }

                        } catch (e: Exception) {

                            errorMessage = "Unable to load podcasts."

                        } finally {

                            isLoading = false
                            hasSearched = true
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Search Podcasts")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            CircularProgressIndicator()
        }

        if (errorMessage.isNotEmpty()) {
            Text(errorMessage)
        }
        // Shows a helpful message when a completed search has no matching podcasts
        if (hasSearched &&
            !isLoading &&
            errorMessage.isEmpty() &&
            podcasts.isEmpty()
        ) {
            Text("No podcasts found. Try another search.")
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {

            items(podcasts) { podcast ->

                PodcastItem(podcast)

                HorizontalDivider()

            }
        }
    }
}

@Composable
fun PodcastItem(podcast: Podcast) {

    // Keeps track of whether this podcast is subscribed
    var isSubscribed by remember { mutableStateOf(false) }

    val context = androidx.compose.ui.platform.LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {

        Text(
            text = podcast.collectionName,
            fontSize = 18.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "By ${podcast.artistName}",
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Episodes: ${podcast.trackCount}",
            fontSize = 13.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row {

            Button(
                onClick = {
                    isSubscribed = !isSubscribed
                }
            ) {
                Text(
                    if (isSubscribed) "Subscribed ✓"
                    else "Subscribe"
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = {
                    podcast.collectionViewUrl?.let { url ->
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(url)
                        )
                        context.startActivity(intent)
                    }
                }
            ) {
                Text("▶ Open Podcast")
            }
        }
    }
}