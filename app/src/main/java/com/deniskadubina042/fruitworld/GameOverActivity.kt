package com.deniskadubina042.fruitworld

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.deniskadubina042.fruitworld.databinding.ActivityGameOverBinding

class GameOverActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityGameOverBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

    }

    @SuppressLint("SetTextI18n")
    override fun onStart() {
        super.onStart()

        binding.scoreView.text = "Count: ${intent.getIntExtra("score",0)}"

        binding.apply {
            restartButtonView.setOnClickListener {
                startActivity(Intent(this@GameOverActivity, MainActivity::class.java))
                overridePendingTransition(0,0)
                finish()
            }
            menuButtonView.setOnClickListener {
                startActivity(Intent(this@GameOverActivity, MainMenuActivity::class.java))
                overridePendingTransition(0,0)
                finish()
            }

        }
    }

    override fun onBackPressed() {
        startActivity(Intent(this@GameOverActivity, MainMenuActivity::class.java))
        overridePendingTransition(0,0)
        finish()
    }
}