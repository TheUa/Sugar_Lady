package com.candyworld.sugarlady

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import com.candyworld.sugarlady.databinding.ActivityMainBinding
import com.candyworld.sugarlady.databinding.CustomToolbarGameBinding
import com.candyworld.sugarlady.gameObjects.Elements
import com.candyworld.sugarlady.gameObjects.ElementsToolbar
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    val displayMetrics by lazy {
        resources.displayMetrics
    }
    private var _count = 0
    private var countdownTimer: CountDownTimer? = null
    private var live = 4
    private lateinit var toolbar: CustomToolbarGameBinding
    var screenWidth by Delegates.notNull<Int>()
    var screenHeight by Delegates.notNull<Int>()
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        enableEdgeToEdge()
        setSupportActionBar(binding.includedToolbar.toolbarView)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar = binding.includedToolbar
        screenWidth = displayMetrics.widthPixels
        screenHeight = displayMetrics.heightPixels

        binding.countView.text = "Count: $_count"
    }

    override fun onStart() {
        super.onStart()

        binding.apply {
            imageView.setImageBitmap(Elements(resources).getRandomBitmapFruit())
            Elements(resources).setRandomPositionBitmapElement(imageView, screenWidth, screenHeight)
            startCountdownTimer(imageView)
            toolbar.backButtonView.setOnClickListener {
                onBackPressed()
            }

        }
    }

    private fun startCountdownTimer(imageView: ImageView) {
        countdownTimer = object : CountDownTimer(3000, 1000) {
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                imageView.setOnClickListener {
                    _count++
                    binding.countView.text = "Count: $_count"
                    countdownTimer?.cancel()
                    imageView.setImageBitmap(Elements(resources).getRandomBitmapFruit())
                    Elements(resources).setRandomPositionBitmapElement(imageView, screenWidth, screenHeight)
                    startCountdownTimer(imageView)
                }

            }

            override fun onFinish() {
                binding.apply {
                    imageView.setImageBitmap(Elements(resources).getBitmapBrokenHeart())
                }
                live--
                when(live){
                    3 ->  toolbar.firstHeartLifeView.setImageBitmap(ElementsToolbar(resources).getBitmapToolbarHeartMinus())
                    2 ->  toolbar.secondHeartLifeView.setImageBitmap(ElementsToolbar(resources).getBitmapToolbarHeartMinus())
                    1 ->  toolbar.thirdHeartLifeView.setImageBitmap(ElementsToolbar(resources).getBitmapToolbarHeartMinus())
                }
                if(live <= 0){
                    val intent = Intent(this@MainActivity, GameOverActivity::class.java)
                    intent.putExtra("score", _count)
                    startActivity(intent)
                    overridePendingTransition(0,0)
                    finish()
                }


            }
        }
        countdownTimer?.start()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(0,0)
    }
}