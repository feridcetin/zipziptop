package com.feridcetin.zipziptop

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
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

    // Giriş Reklamı (Interstitial) için değişken
    private var mInterstitialAd: InterstitialAd? = null
    // Giriş Reklamı TEST Kimliği:
    private val INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-2120666198065087/4581061889"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1. AdMob'u Başlatma (Uygulama açılışında bir kez)
        MobileAds.initialize(this) {}

        // 2. 🚨 KESİN ÇÖZÜM: TEST CİHAZI AYARI 🚨
        val testDeviceIds = Arrays.asList("A976EF03B07D5C3A2F0E473A2462CD98") // Log'da gördüğünüz ID
        val configuration = RequestConfiguration.Builder()
            .setTestDeviceIds(testDeviceIds)
            .build()
        MobileAds.setRequestConfiguration(configuration)

        // Buton ve AdView Tanımlamaları
        val btnPlay: Button = findViewById(R.id.btn_play)
        val btnSettings: Button = findViewById(R.id.btn_settings)
        val btnExit: Button = findViewById(R.id.btn_exit)
        val btnScoreboard: Button = findViewById(R.id.btnScoreboard)

        adViewBottom = findViewById(R.id.adViewBottom)

        // 2. Reklam Yüklemeleri
        loadBannerAds()       // Banner reklamlar hemen yüklenir
        loadInterstitialAd()  // Giriş reklamı arka planda hazır tutulur

        // Listener'lar
        btnPlay.setOnClickListener {
            // OYNA butonuna basıldığında, reklamı gösterip kapandıktan sonra oyunu başlat.
            showInterstitialAd()
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

    // Giriş Reklamını Yükleme Metodu (Arka planda hazır tutar)
    private fun loadInterstitialAd() {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(this, INTERSTITIAL_AD_UNIT_ID, adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.e("InterstitialAd", "Giriş Reklamı yüklenemedi: ${adError.message}")
                mInterstitialAd = null
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                Log.d("InterstitialAd", "Giriş Reklamı başarıyla yüklendi.")
                mInterstitialAd = interstitialAd
            }
        })
    }

    // Giriş Reklamını Gösterme Metodu
    private fun showInterstitialAd() {
        if (mInterstitialAd != null) {
            // Reklam hazırsa gösterilir ve kapandıktan sonra oyun başlar (Politikaya uygun)
            mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {

                override fun onAdDismissedFullScreenContent() {
                    Log.d("InterstitialAd", "Reklam kapatıldı, oyun başlıyor.")
                    startGame() // Oyun başlatıldı
                    loadInterstitialAd() // Bir sonraki oyun için reklamı tekrar yükle
                }

                override fun onAdFailedToShowFullScreenContent(adError: com.google.android.gms.ads.AdError) {
                    Log.e("InterstitialAd", "Reklam gösterilemedi: " + adError.message)
                    startGame() // Hata olsa bile kullanıcıyı engelleme, oyunu başlat
                }

                override fun onAdShowedFullScreenContent() {
                    Log.d("InterstitialAd", "Giriş Reklamı gösterildi.")
                }
            }
            mInterstitialAd?.show(this)
        } else {
            // ÖNEMLİ DÜZELTME: Reklam hazır değilse oyunu hemen başlatmayız.
            Log.d("InterstitialAd", "Giriş Reklamı henüz hazır değil.")
            // Kullanıcı tekrar denemesi için beklerken reklamı yeniden yüklemeyi deneriz.
            loadInterstitialAd()
        }
    }

    // Oyunu başlatan yardımcı metot
    private fun startGame() {
        val intent = Intent(this, GameActivity::class.java)
        // Yeni bir oyun seansı başlattığımızı GameActivity'ye bildiriyoruz
        intent.putExtra("isFirstGameSession", true)
        startActivity(intent)
    }
}