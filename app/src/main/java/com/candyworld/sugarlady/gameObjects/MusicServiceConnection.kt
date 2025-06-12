package com.candyworld.sugarlady.gameObjects

interface MusicServiceConnection {

    fun playMusic()
    fun pauseMusic()
    fun isPlaying(): Boolean
}