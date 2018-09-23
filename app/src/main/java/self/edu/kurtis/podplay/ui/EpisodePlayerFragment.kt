package self.edu.kurtis.podplay.ui

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_episode_player.*
import self.edu.kurtis.podplay.R
import self.edu.kurtis.podplay.util.HtmlUtils
import self.edu.kurtis.podplay.viewmodel.PodcastViewModel

class EpisodePlayerFragment: Fragment() {
    private lateinit var podcastViewModel: PodcastViewModel

    companion object {
        fun newInstance(): EpisodePlayerFragment {
            return EpisodePlayerFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        setupViewModel()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_episode_player, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        updateControls()
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onStop() {
        super.onStop()
    }

    private fun setupViewModel() {
        podcastViewModel = ViewModelProviders.of(activity).get(PodcastViewModel::class.java)
    }

    private fun updateControls() {
        episodeTitleTextView.text = podcastViewModel.activeEpisodeViewData?.title

        val htmlDesc = podcastViewModel.activeEpisodeViewData?.description ?: ""
        val descSpan = HtmlUtils.htmlToSpannable(htmlDesc)
        episodeDescTextView.text = descSpan
        episodeDescTextView.movementMethod = ScrollingMovementMethod()

        Glide.with(activity).load(podcastViewModel.activePodcastViewData?.imageUrl).into(episodeImageView)

    }
}