package com.deniskadubina042.fruitworld.gameObjects

interface MusicServiceConnection {

    fun playMusic()
    fun pauseMusic()
    fun isPlaying(): Boolean
}