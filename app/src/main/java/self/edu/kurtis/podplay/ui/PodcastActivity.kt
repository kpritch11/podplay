package self.edu.kurtis.podplay.ui

import android.app.SearchManager
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.support.v7.widget.SearchView
import android.view.View
import self.edu.kurtis.podplay.R
import self.edu.kurtis.podplay.repository.ItunesRepo
import self.edu.kurtis.podplay.service.ItunesService
import kotlinx.android.synthetic.main.activity_podcast.*
import self.edu.kurtis.podplay.adapter.PodcastListAdapter
import self.edu.kurtis.podplay.viewmodel.SearchViewModel

class PodcastActivity : AppCompatActivity(), PodcastListAdapter.PodcastListAdapterListener {

    val TAG = javaClass.simpleName
    private lateinit var searchViewModel: SearchViewModel
    private lateinit var podcastListAdapter: PodcastListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_podcast)

        setupToolbar()
        setupViewModels()
        updateControls()
    }

    override fun onCreateOptionsMenu(menu: Menu) : Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_search, menu)

        val searchMenuItem = menu.findItem(R.id.search_item)
        val searchView = searchMenuItem?.actionView as SearchView
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager

        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))

        return true
    }

    private fun performSearch(term: String) {
        showProgressBar()
        searchViewModel.searchPodcasts(term) { results ->
            hideProgressBar()
            toolbar.title = getString(R.string.search_results)
            podcastListAdapter.setSearchData(results)
        }
    }

    private fun handleIntent(intent: Intent) {
        if (Intent.ACTION_SEARCH == intent.action) {
            val query = intent.getStringExtra(SearchManager.QUERY)
            performSearch(query)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
    }

    private fun setupViewModels() {
        val service = ItunesService.instance
        searchViewModel = ViewModelProviders.of(this).get(SearchViewModel::class.java)
        searchViewModel.iTunesRepo = ItunesRepo(service)
    }

    private fun updateControls() {
        podcastRecyclerView.setHasFixedSize(true)

        val layoutManager = LinearLayoutManager(this)
        podcastRecyclerView.layoutManager = layoutManager

        val dividerItemDecoration = android.support.v7.widget.DividerItemDecoration(podcastRecyclerView.context, layoutManager.orientation)
        podcastRecyclerView.addItemDecoration(dividerItemDecoration)

        podcastListAdapter = PodcastListAdapter(null, this, this)
        podcastRecyclerView.adapter = podcastListAdapter
    }

    override fun onShowDetails(podcastSummaryViewData: SearchViewModel.PodcastSummaryViewData) {
        // Not implemented yet
    }

    private fun showProgressBar() {
        progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        progressBar.visibility = View.INVISIBLE
    }
}
