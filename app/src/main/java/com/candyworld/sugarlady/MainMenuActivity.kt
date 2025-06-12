package com.candyworld.sugarlady

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import androidx.activity.enableEdgeToEdge
import com.candyworld.sugarlady.databinding.ActivityMainMenuBinding
import com.candyworld.sugarlady.gameObjects.BackgroundMusicService
import com.candyworld.sugarlady.gameObjects.MusicServiceConnection

class MainMenuActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityMainMenuBinding.inflate(layoutInflater)
    }
    var mediaCheck = true
    private val intentService by lazy {
        Intent(this, BackgroundMusicService::class.java)
    }
    private var musicService: MusicServiceConnection? = null
    private var serviceBound = false
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as BackgroundMusicService.MusicBinder
            musicService = binder.getService()
            serviceBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            musicService = null
            serviceBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        enableEdgeToEdge()
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        startPlayingMP()
        binding.includedToolbar.soundButton.setImageBitmap(Bitmap.createBitmap(BitmapFactory.decodeResource(resources, R.drawable.sound_on)))
        binding.includedToolbar.exitButton.setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        if (musicService != null) {
            if (!musicService!!.isPlaying()) {
                musicService!!.playMusic()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        binding.apply {
            includedToolbar.soundButton.setOnClickListener {
                if(!mediaCheck){
                   startPlayingMP()
                } else {
                    stopPlayingMP()
                }
            }
            startGame.setOnClickListener {
                startActivity(Intent(this@MainMenuActivity, MainActivity::class.java))
                overridePendingTransition(0,0)
            }
        }
    }
    private fun startPlayingMP(){
        startService(intentService)
        mediaCheck = true
        binding.includedToolbar.soundButton.setImageBitmap(Bitmap.createBitmap(BitmapFactory.decodeResource(resources, R.drawable.sound_on)))
    }

    override fun onPause() {
        super.onPause()
        if (musicService != null) {
            if (musicService!!.isPlaying()) {
                musicService!!.pauseMusic()
            }
        }
    }
    fun stopPlayingMP(){
        stopService(intentService)
        mediaCheck = false
        binding.includedToolbar.soundButton.setImageBitmap(Bitmap.createBitmap(BitmapFactory.decodeResource(resources, R.drawable.sound_off)))
    }
    override fun onDestroy() {
        super.onDestroy()
        if (serviceBound) {
            unbindService(serviceConnection)
            serviceBound = false
        }
    }
}