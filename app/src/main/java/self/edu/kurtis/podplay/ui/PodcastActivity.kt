package self.edu.kurtis.podplay.ui

import android.app.SearchManager
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.support.v7.widget.SearchView
import self.edu.kurtis.podplay.R

class PodcastActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_podcast)
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
}
