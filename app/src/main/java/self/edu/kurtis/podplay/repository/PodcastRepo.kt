package self.edu.kurtis.podplay.repository

import self.edu.kurtis.podplay.model.Podcast
import self.edu.kurtis.podplay.service.RssFeedService

class PodcastRepo {
    fun getPodcast(feedUrl: String, callback: (Podcast?) -> Unit) {
        val rssFeedService = RssFeedService()
        rssFeedService.getFeed(feedUrl) {

        }
        callback(Podcast(feedUrl, "No Name", "No description", "No image"))
    }
}