package com.feridcetin.zipziptop

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView

class ScoreboardActivity : AppCompatActivity() {

    private lateinit var sharedPref: SharedPreferences
    private lateinit var textViewHighScore: TextView
    private lateinit var btnBackToMenu: Button

    private lateinit var adViewTop: AdView
    private lateinit var adViewBottom: AdView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scoreboard)
        adViewTop = findViewById(R.id.adViewTopScore)
        adViewBottom = findViewById(R.id.adViewBottomScore)
        loadBannerAds() // Yeni reklam yükleme metodunu çağırın
        sharedPref = getSharedPreferences("JumpHeroPrefs", Context.MODE_PRIVATE)
        textViewHighScore = findViewById(R.id.textViewHighScore)
        btnBackToMenu = findViewById(R.id.btnBackToMenu)

        loadHighScore()

        btnBackToMenu.setOnClickListener {
            finish()
        }
    }

    private fun loadHighScore() {
        val highScore = sharedPref.getInt("high_score", 0)
        textViewHighScore.text = getString(R.string.high_score, highScore)

    }

    private fun loadBannerAds() {
        val adRequest = AdRequest.Builder().build()

        // Üst reklamı yükle
        adViewTop.loadAd(adRequest)

        // Alt reklamı yükle
        adViewBottom.loadAd(adRequest)
    }
}