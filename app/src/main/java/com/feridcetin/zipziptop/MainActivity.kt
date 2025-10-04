package com.feridcetin.zipziptop

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import java.util.Arrays

class MainActivity : AppCompatActivity() {

    // Banner Reklamlar için değişkenler
    private lateinit var adViewBottom: AdView
    private var mInterstitialAd: InterstitialAd? = null
    private val TAG_INTERSTITIAL = "InterstitialAd"
    private var isAdShown = false // Reklamın sadece bir kez gösterildiğini kontrol eder


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1. AdMob'u Başlatma (Uygulama açılışında bir kez)
        MobileAds.initialize(this) {}

        // 2. 🚨 KESİN ÇÖZÜM: TEST CİHAZI AYARI 🚨
        val testDeviceIds = Arrays.asList("A976EF03B07D5C3A2F0E473A2462CD98") // Log'da gördüğünüz ID
        val configuration = RequestConfiguration.Builder()
            .setTestDeviceIds(testDeviceIds)
            .setMaxAdContentRating(RequestConfiguration.MAX_AD_CONTENT_RATING_G)
            .build()
        MobileAds.setRequestConfiguration(configuration)

        // Buton ve AdView Tanımlamaları
        val btnPlay: Button = findViewById(R.id.btn_play)
        val btnSettings: Button = findViewById(R.id.btn_settings)
        val btnExit: Button = findViewById(R.id.btn_exit)
        val btnScoreboard: Button = findViewById(R.id.btnScoreboard)


        loadInterstitialAd() // Reklamı arka planda yüklemeye başla
        //showInterstitialAd() // Yüklü ise hemen göster (ya da bir buton tıkında çağırılabilir)

        adViewBottom = findViewById(R.id.adViewBottom)

        // 2. Reklam Yüklemeleri
        loadBannerAds()       // Banner reklamlar hemen yüklenir
        // Listener'lar
        btnPlay.setOnClickListener {
            // OYNA butonuna basıldığında, reklamı gösterip kapandıktan sonra oyunu başlat.
            //showInterstitialAd()
            startGame()
        }

        btnSettings.setOnClickListener {
            val intent = Intent(this, SettingActivity::class.java)
            startActivity(intent)
        }

        btnScoreboard.setOnClickListener {
            val intent = Intent(this, ScoreboardActivity::class.java)
            startActivity(intent)
        }

        btnExit.setOnClickListener {
            finishAffinity()
        }
    }

    // Banner Reklamları Yükleme Metodu
    private fun loadBannerAds() {
        val adRequest = AdRequest.Builder().build()
        adViewBottom.loadAd(adRequest)
    }

    // Oyunu başlatan yardımcı metot
    private fun startGame() {
        val intent = Intent(this, GameActivity::class.java)
        // Yeni bir oyun seansı başlattığımızı GameActivity'ye bildiriyoruz
        intent.putExtra("isFirstGameSession", true)
        startActivity(intent)
    }

    private fun showInterstitialAd() {
        if (mInterstitialAd != null && !isAdShown) {

            mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    Log.d(TAG_INTERSTITIAL, "Reklam kapatıldı.")
                    mInterstitialAd = null
                    loadInterstitialAd() // Yeni reklam yükle
                }

                override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                    Log.e(TAG_INTERSTITIAL, "Reklam gösterimi başarısız: " + p0.message)
                }
            }

            mInterstitialAd?.show(this)
            isAdShown = true // Reklamı gösterdikten sonra bayrağı true yap

        } else {
            Log.d(TAG_INTERSTITIAL, "Reklam henüz yüklenmedi veya daha önce gösterildi.")
        }
    }

    private fun loadInterstitialAd() {
        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(this, getString(R.string.interstitial_ad_unit_id), adRequest, object : InterstitialAdLoadCallback() {

            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.d(TAG_INTERSTITIAL, adError.message)
                mInterstitialAd = null
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                Log.d(TAG_INTERSTITIAL, "Reklam yüklendi.")
                mInterstitialAd = interstitialAd

                // YENİ EKLEME: Reklam yüklendiği anda gösterme metodunu çağır
                showInterstitialAd()
            }
        })
    }
}