package self.edu.kurtis.podplay.service

import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaBrowserServiceCompat
import android.support.v4.media.session.MediaSessionCompat

class PodplayMediaService: MediaBrowserServiceCompat() {
    private lateinit var mediaSession: MediaSessionCompat

    override fun onCreate() {
        super.onCreate()
        createMediaSession()
    }

    override fun onLoadChildren(parentId: String, result: Result<MutableList<MediaBrowserCompat.MediaItem>>) {
        if (parentId.equals(PODPLAY_EMPTY_ROOT_MEDIA_ID)) {
            result.sendResult(null)
        }
    }

    override fun onGetRoot(clientPackageName: String, clienutUid: Int, rootHints: Bundle?): BrowserRoot? {
        return MediaBrowserServiceCompat.BrowserRoot(PODPLAY_EMPTY_ROOT_MEDIA_ID, null)
    }

    private fun createMediaSession() {
        mediaSession = MediaSessionCompat(this, "PodplayMediaService")
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)
        setSessionToken(mediaSession.sessionToken)
        val callback = PodplayMediaCallback(this, mediaSession)
        mediaSession.setCallback(callback)
    }

    companion object {
        private const val PODPLAY_EMPTY_ROOT_MEDIA_ID = "podplay_empty_root_media_id"
    }
}