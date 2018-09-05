package self.edu.kurtis.podplay.repository

import self.edu.kurtis.podplay.model.Podcast

class PodcastRepo {
    fun getPodcast(feedUrl: String, callback: (Podcast?) -> Unit) {
        callback(Podcast(feedUrl, "No Name", "No description", "No image"))
    }
}