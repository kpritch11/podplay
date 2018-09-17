package self.edu.kurtis.podplay.service

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.support.v4.media.session.MediaSessionCompat

class PodplayMediaCallback(val context: Context, val mediaSession: MediaSessionCompat, var mediaPlayer: MediaPlayer? = null): MediaSessionCompat.Callback() {
    override fun onPlayFromUri(uri: Uri?, extras: Bundle?) {
        super.onPlayFromUri(uri, extras)
        println("Playing ${uri.toString()}")
        onPlay()
    }

    override fun onPlay() {
        super.onPlay()
        println("onPlay called")
    }

    override fun onStop() {
        super.onStop()
        println("onStop called")
    }

    override fun onPause() {
        super.onPause()
        println("onPause called")
    }
}