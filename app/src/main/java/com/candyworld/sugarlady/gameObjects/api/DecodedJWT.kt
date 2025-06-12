package com.candyworld.sugarlady.gameObjects.api

import android.util.Base64
import org.json.JSONObject
 class DecodedJWT(

) {
     fun decodeJWTObject(jwt: String): JSONObject? {
         return try {
             val parts = jwt.split(".")
             if (parts.size != 3) throw IllegalArgumentException("Invalid JWT token")
             val payload = parts[1]

             val decodedBytes = Base64.decode(payload, Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP)
             val decodedString = String(decodedBytes, Charsets.UTF_8)

             JSONObject(decodedString)
         } catch (e: Exception) {
             e.printStackTrace()
             null
         }
     }
 }

