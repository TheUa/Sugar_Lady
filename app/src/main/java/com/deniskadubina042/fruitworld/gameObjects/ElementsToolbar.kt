package com.deniskadubina042.fruitworld.gameObjects

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.deniskadubina042.fruitworld.R

class ElementsToolbar(private val resources: Resources) {

    fun getBitmapToolbarHeartMinus(): Bitmap{
        return Bitmap.createBitmap(BitmapFactory.decodeResource(resources, R.drawable.heart_transparent))
    }
}