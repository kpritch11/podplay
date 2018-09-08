package self.edu.kurtis.podplay.repository

import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import self.edu.kurtis.podplay.model.Episode
import self.edu.kurtis.podplay.model.Podcast
import self.edu.kurtis.podplay.service.FeedService
import self.edu.kurtis.podplay.service.RssFeedResponse
import self.edu.kurtis.podplay.util.DateUtils

class PodcastRepo(private var feedService: FeedService) {
    fun getPodcast(feedUrl: String, callback: (Podcast?) -> Unit) {
        feedService.getFeed(feedUrl) { feedResponse ->
            var podcast: Podcast? = null
            if (feedResponse != null) {
                podcast = rssResponseToPodcast(feedUrl, "", feedResponse)
            }

            launch(UI) {
                callback(podcast)
            }
        }
    }

    private fun rssItemsToEpisodes(episodeResponses: List<RssFeedResponse.EpisodeResponse>) : List<Episode> {
        return episodeResponses.map {
            Episode(
                    it.guid ?: "",
                    it.title ?: "",
                    it.description ?: "",
                    it.url ?: "",
                    it.type ?: "",
                    DateUtils.xmlDateToDate(it.pubDate),
                    it.duration ?: ""
            )
        }
    }

    private fun rssResponseToPodcast(feedUrl: String, imageUrl: String, rssResponse: RssFeedResponse) : Podcast? {
        val items = rssResponse.episodes ?: return null
        val description = if (rssResponse.description == "") rssResponse.summary else rssResponse.description
        return Podcast(feedUrl, rssResponse.title, description, imageUrl, rssResponse.lastUpdated, episodes = rssItemsToEpisodes(items))
    }
}