package self.edu.kurtis.podplay.db

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import android.arch.persistence.room.Query
import self.edu.kurtis.podplay.model.Episode
import self.edu.kurtis.podplay.model.Podcast

@Dao
interface PodcastDao {
    @Query("SELECT * FROM Podcast ORDER BY FeedTitle")
    fun loadPodcasts(): LiveData<List<Podcast>>

    @Query("SELECT * FROM Episode WHERE podcastId = :podcastId ORDER BY releaseDate DESC")
    fun loadEpisodes(podcastId: Long): List<Episode>

    @Insert(onConflict = REPLACE)
    fun insertPodcast(podcast: Podcast): Long

    @Insert(onConflict = REPLACE)
    fun insertEpisode(episode: Episode): Long
}