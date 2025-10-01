package com.feridcetin.zipziptop

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
class MainActivity : AppCompatActivity() {

    private lateinit var playButton: Button
    private lateinit var settingsButton: Button
    private lateinit var exitButton: Button

    private lateinit var scoreboardButton: Button

    private lateinit var adViewTop: AdView
    private lateinit var adViewBottom: AdView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Banner reklam kodlarının eklenmesi
        // AdView'leri layout'tan bulup reklamları yükleyin
        adViewTop = findViewById(R.id.adViewTop)
        adViewBottom = findViewById(R.id.adViewBottom)
        loadBannerAds() // Yeni reklam yükleme metodunu çağırın


        playButton = findViewById(R.id.btn_play)
        settingsButton = findViewById(R.id.btn_settings)
        exitButton = findViewById(R.id.btn_exit)
        scoreboardButton = findViewById(R.id.btnScoreboard)

        // Buton tıklama dinleyicilerini ayarlama
        playButton.setOnClickListener {
            val intent = Intent(this, GameActivity::class.java)
            // Yeni bir oyun seansı başlattığımızı GameActivity'ye bildiriyoruz
            intent.putExtra("isFirstGameSession", true)
            startActivity(intent)
        }

        settingsButton.setOnClickListener {
            val intent = Intent(this, SettingActivity::class.java)
            startActivity(intent)
        }

        exitButton.setOnClickListener {
            finishAffinity()
        }

        scoreboardButton.setOnClickListener {
            val intent = Intent(this, ScoreboardActivity::class.java)
            startActivity(intent)
        }
    }


    private fun loadBannerAds() {
        val adRequest = AdRequest.Builder().build()

        // Üst reklamı yükle
        adViewTop.loadAd(adRequest)

        // Alt reklamı yükle
        adViewBottom.loadAd(adRequest)
    }

}