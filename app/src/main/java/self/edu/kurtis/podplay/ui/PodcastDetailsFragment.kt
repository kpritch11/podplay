package self.edu.kurtis.podplay.ui

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.text.method.ScrollingMovementMethod
import android.view.*
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_podcast_details.*
import self.edu.kurtis.podplay.R
import self.edu.kurtis.podplay.adapter.EpisodeListAdapter
import self.edu.kurtis.podplay.viewmodel.PodcastViewModel

class PodcastDetailsFragment : Fragment() {
    private lateinit var podcastViewModel: PodcastViewModel
    private lateinit var episodeListAdapter: EpisodeListAdapter
    private var listener: OnPodcastDetailsListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        setupViewModel()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?) : View? {
        return inflater!!.inflate(R.layout.fragment_podcast_details, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupControls()
        updateControls()
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.menu_details, menu)
    }

    private fun setupViewModel() {
        podcastViewModel = ViewModelProviders.of(activity).get(PodcastViewModel::class.java)
    }

    private fun updateControls() {
        val viewData = podcastViewModel.activePodcastViewData ?: return
        feedTitleTextView.text = viewData.feedTitle
        feedDescTextView.text = viewData.feedDesc
        Glide.with(activity).load(viewData.imageUrl).into(feedImageView)
    }

    companion object {
        fun newInstance() : PodcastDetailsFragment {
            return PodcastDetailsFragment()
        }
    }

    private fun setupControls() {
        feedDescTextView.movementMethod = ScrollingMovementMethod()
        episodeRecyclerView.setHasFixedSize(true)

        val layoutManager = LinearLayoutManager(activity)
        episodeRecyclerView.layoutManager = layoutManager

        val dividerItemDecoration = android.support.v7.widget.DividerItemDecoration(episodeRecyclerView.context, layoutManager.orientation)
        episodeRecyclerView.addItemDecoration(dividerItemDecoration)
        episodeListAdapter = EpisodeListAdapter(podcastViewModel.activePodcastViewData?.episodes)
        episodeRecyclerView.adapter = episodeListAdapter
    }

    interface OnPodcastDetailsListener {
        fun onSubscribe()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnPodcastDetailsListener) {
            listener = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnPodcastDetailsListener")
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_feed_action -> {
                podcastViewModel.activePodcastViewData?.feedUrl?.let {
                    listener?.onSubscribe()
                }
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}
