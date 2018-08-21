package self.edu.kurtis.podplay.ui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import self.edu.kurtis.podplay.R
import self.edu.kurtis.podplay.repository.ItunesRepo
import self.edu.kurtis.podplay.service.ItunesService
import self.edu.kurtis.podplay.service.PodcastResponse

class PodcastActivity : AppCompatActivity() {

    val TAG = javaClass.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_podcast)

        val itunesService = ItunesService.instance
        val itunesRepo = ItunesRepo(itunesService)

        itunesRepo.searchByTerm("Android Developer") {
            Log.i(TAG, "Results = $it")
        }
    }
}
