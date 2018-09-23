package self.edu.kurtis.podplay.ui

import android.arch.lifecycle.ViewModelProviders
import android.content.ComponentName
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_episode_player.*
import self.edu.kurtis.podplay.R
import self.edu.kurtis.podplay.service.PodplayMediaCallback.Companion.CMD_CHANGESPEED
import self.edu.kurtis.podplay.service.PodplayMediaCallback.Companion.CMD_EXTRA_SPEED
import self.edu.kurtis.podplay.service.PodplayMediaService
import self.edu.kurtis.podplay.util.HtmlUtils
import self.edu.kurtis.podplay.viewmodel.PodcastViewModel

class EpisodePlayerFragment: Fragment() {
    private lateinit var podcastViewModel: PodcastViewModel
    private lateinit var mediaBrowser: MediaBrowserCompat
    private var mediaControllerCallback: MediaControllerCallback? = null
    private var playerSpeed: Float = 1.0f

    companion object {
        fun newInstance(): EpisodePlayerFragment {
            return EpisodePlayerFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        setupViewModel()
        initMediaBrowser()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_episode_player, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupControls()
        updateControls()
    }

    override fun onStart() {
        super.onStart()
        if (mediaBrowser.isConnected) {
            if (MediaControllerCompat.getMediaController(activity) == null) {
                registerMediaController(mediaBrowser.sessionToken)
            }
        } else {
            mediaBrowser.connect()
        }
    }

    override fun onStop() {
        super.onStop()
        if (MediaControllerCompat.getMediaController(activity) != null) {
            mediaControllerCallback?.let {
                MediaControllerCompat.getMediaController(activity).unregisterCallback(it)
            }
        }
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

        speedButton.text = "${playerSpeed}"
    }

    private fun startPlaying(episodeViewData: PodcastViewModel.EpisodeViewData) {
        val controller = MediaControllerCompat.getMediaController(activity)
        val viewData = podcastViewModel.activePodcastViewData ?: return
        val bundle = Bundle()
        bundle.putString(MediaMetadataCompat.METADATA_KEY_TITLE, episodeViewData.title)
        bundle.putString(MediaMetadataCompat.METADATA_KEY_ARTIST, viewData.feedTitle)
        bundle.putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, viewData.imageUrl)
        controller.transportControls.playFromUri(Uri.parse(episodeViewData.mediaUrl), bundle)
    }

    inner class MediaBrowserCallbacks: MediaBrowserCompat.ConnectionCallback() {
        override fun onConnected() {
            super.onConnected()
            registerMediaController(mediaBrowser.sessionToken)
            println("onConnected")
        }

        override fun onConnectionSuspended() {
            super.onConnectionSuspended()
            println("onConnectionSuspended")
        }

        override fun onConnectionFailed() {
            super.onConnectionFailed()
            println("onConnectionFailed")
        }
    }

    inner class MediaControllerCallback: MediaControllerCompat.Callback() {
        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            println("metadata changed to ${metadata?.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI)}")
        }

        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            val state = state ?: return
            handleStateChange(state.getState())
        }
    }

    private fun initMediaBrowser() {
        mediaBrowser = MediaBrowserCompat(activity, ComponentName(activity, PodplayMediaService::class.java), MediaBrowserCallbacks(), null)
    }

    private fun registerMediaController(token: MediaSessionCompat.Token) {
        val mediaController = MediaControllerCompat(activity, token)
        MediaControllerCompat.setMediaController(activity, mediaController)
        mediaControllerCallback = MediaControllerCallback()
        mediaController.registerCallback(mediaControllerCallback!!)
    }

    private fun togglePlayPause() {
        val controller = MediaControllerCompat.getMediaController(activity)
        if (controller.playbackState != null) {
            if (controller.playbackState.state == PlaybackStateCompat.STATE_PLAYING) {
                controller.transportControls.pause()
            } else {
                podcastViewModel.activeEpisodeViewData?.let { startPlaying(it) }
            }
        } else {
            podcastViewModel.activeEpisodeViewData?.let { startPlaying(it) }
        }
    }

    private fun setupControls() {
        playToggleButton.setOnClickListener {
            togglePlayPause()
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            speedButton.setOnClickListener {
                changeSpeed()
            }
        } else {
            speedButton.visibility = View.INVISIBLE
        }
        forwardButton.setOnClickListener {
            seekBy(30)
        }
        replayButton.setOnClickListener {
            seekBy(-10)
        }
    }

    private fun handleStateChange(state: Int) {
        val isPlaying = state == PlaybackStateCompat.STATE_PLAYING
        playToggleButton.isActivated = isPlaying
    }

    private fun changeSpeed() {
        playerSpeed += 0.25f
        if (playerSpeed > 2.0f) {
            playerSpeed = 0.75f
        }
        val bundle = Bundle()
        bundle.putFloat(CMD_EXTRA_SPEED, playerSpeed)
        val controller = MediaControllerCompat.getMediaController(activity)
        controller.sendCommand(CMD_CHANGESPEED, bundle, null)
        speedButton.text = "${playerSpeed}"
    }

    private fun seekBy(seconds: Int) {
        val controller = MediaControllerCompat.getMediaController(activity)
        val newPosition = controller.playbackState.position + seconds*1000
        controller.transportControls.seekTo(newPosition)
    }
}