package com.candyworld.sugarlady.gameObjects

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import com.candyworld.sugarlady.R

class BackgroundMusicService : Service(), MusicServiceConnection {
    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer.create(this, R.raw.background_sound)
        mediaPlayer.isLooping = true
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!mediaPlayer.isPlaying) {
            mediaPlayer.start()
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        return MusicBinder()
    }
    inner class MusicBinder : Binder() {
        fun getService(): BackgroundMusicService {
            return this@BackgroundMusicService
        }
    }

    override fun onDestroy() {
        mediaPlayer.stop()
        mediaPlayer.release()
        super.onDestroy()
    }

    override fun playMusic() {
        TODO("Not yet implemented")
    }

    override fun pauseMusic() {
        TODO("Not yet implemented")
    }

    override fun isPlaying(): Boolean {
        TODO("Not yet implemented")
    }
}