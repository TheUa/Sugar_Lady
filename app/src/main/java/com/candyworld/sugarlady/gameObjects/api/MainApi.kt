package com.candyworld.sugarlady.gameObjects.api

import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Url

interface MainApi {
    @POST
    fun sendCampLink(@Url url: String, @Body jsonObject: JsonObject): Call<ResponseCapy>
}