package com.candyworld.sugarlady.gameObjects

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.candyworld.sugarlady.R

class ElementsToolbar(private val resources: Resources) {

    fun getBitmapToolbarHeartMinus(): Bitmap{
        return Bitmap.createBitmap(BitmapFactory.decodeResource(resources, R.drawable.broken_heart))
    }
}