package com.candyworld.sugarlady.gameObjects

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.ImageView
import com.candyworld.sugarlady.R
import java.security.SecureRandom

class Elements(private val resources: Resources) {
    private val listOfFruits = listOf(
        R.drawable.fr1,
        R.drawable.fr2,
        R.drawable.fr3,
        R.drawable.fr4,
        R.drawable.fr5,
        R.drawable.fr6,
        R.drawable.fr7,
        R.drawable.fr8,
        R.drawable.fr9,
        R.drawable.fr10,
        R.drawable.fr11,
        R.drawable.fr12,
        R.drawable.fr13,
        R.drawable.fr14,
        R.drawable.fr15,
        R.drawable.fr16
    )
    private val secureRandom = SecureRandom()

    fun getRandomBitmapFruit(): Bitmap{
       val decodeRes =  BitmapFactory.decodeResource(resources,listOfFruits[secureRandom.nextInt(listOfFruits.size)])
        return Bitmap.createScaledBitmap(decodeRes, 250, 250, false)
    }

    fun setRandomPositionBitmapElement(imageView: ImageView, screenWidth: Int, screenHeight: Int){
        var randomX = secureRandom.nextInt(screenWidth - imageView.width)
        var randomY = secureRandom.nextInt(screenHeight - imageView.height)
        if (randomX < 50f) {
            randomX = 0
        }
        if (randomY < 0 || randomY > 1300f) {
            randomY = 0
        }

        if (randomX + imageView.width > screenWidth) {
            randomX = screenWidth - imageView.width
        }
        if (randomY + imageView.height > screenHeight) {
            randomY = screenHeight - imageView.height
        }
        imageView.x = randomX.toFloat()
        imageView.y = randomY.toFloat()
    }

    fun getBitmapBrokenHeart(): Bitmap{
        val decodeRes =  BitmapFactory.decodeResource(resources,R.drawable.broken_heart)
        return Bitmap.createScaledBitmap(decodeRes, 200, 150, false)
    }



}