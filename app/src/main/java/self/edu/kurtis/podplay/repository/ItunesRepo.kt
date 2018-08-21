package self.edu.kurtis.podplay.repository

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import self.edu.kurtis.podplay.service.ItunesService
import self.edu.kurtis.podplay.service.PodcastResponse

class ItunesRepo(private val itunesService: ItunesService) {
    fun searchByTerm(term: String, callBack: (List<PodcastResponse.ItunesPodcast>?) -> Unit) {
        val podcastCall = itunesService.searchPodcastByTerm(term)
        podcastCall.enqueue(object : Callback<PodcastResponse> {
            override fun onFailure(call: Call<PodcastResponse>?, t: Throwable?) {
                callBack(null)
            }
            override fun onResponse(call: Call<PodcastResponse>?, response: Response<PodcastResponse>?) {
                val body = response?.body()
                callBack(body?.results)
            }
        })
    }
}