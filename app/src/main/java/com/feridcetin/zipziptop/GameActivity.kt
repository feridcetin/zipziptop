package com.feridcetin.zipziptop


import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.RelativeLayout
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.FullScreenContentCallback // Gerekirse eklemek faydalı
import com.google.android.gms.ads.OnUserEarnedRewardListener


class GameActivity : AppCompatActivity() {

    private lateinit var gameView: GameView
    private var mRewardedAd: RewardedAd? = null

    private lateinit var adViewBottom: AdView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        MobileAds.initialize(this) {}


        adViewBottom = findViewById(R.id.adViewBottom)
        val adRequestBottom = AdRequest.Builder().build()
        adViewBottom.loadAd(adRequestBottom)


        // Reklam dinleyicilerini ekleyin
        adViewBottom.adListener = object : AdListener() {
            override fun onAdLoaded() {
                adViewBottom.visibility = View.VISIBLE
            }
            override fun onAdFailedToLoad(adError: LoadAdError) {
                adViewBottom.visibility = View.GONE
            }
        }


        gameView = GameView(this)
        findViewById<FrameLayout>(R.id.gameContainer).addView(gameView)

        loadRewardedAd()

        // Geri tuşu hareketleri için OnBackPressedCallback ekleme
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (gameView.getPaused()) {
                    // Oyun duraklatılmışsa, geri tuşuna basıldığında ana menüye dön
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                } else {
                    // Oyunu duraklat
                    gameView.setPaused(true)
                }
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)
    }
/*
    fun showRewardedAd() {
        if (mRewardedAd != null) {
            mRewardedAd?.show(this) { rewardItem ->
                gameView.grantLifeAndShowResumeDialog()
                loadRewardedAd()
            }
        } else {
            gameView.grantLifeAndShowResumeDialog()
            loadRewardedAd()
        }
    }
 */

    fun showRewardedAd() {
        if (mRewardedAd != null) {
            // Tam ekran geri çağrımını ayarlamak, hata durumlarını yönetmek için önemlidir.
            mRewardedAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                // Reklam gösterilemezse (ekran hatası vb.)
                override fun onAdFailedToShowFullScreenContent(p0: com.google.android.gms.ads.AdError) {
                    Log.e("RewardedAd", "Reklam gösterilemedi: " + p0.message)
                }

                // Reklam kapatıldığında (ödül verilmiş olabilir veya olmamış olabilir)
                override fun onAdDismissedFullScreenContent() {
                    Log.d("RewardedAd", "Reklam kapatıldı.")
                    // Yeni bir reklam yükle
                    loadRewardedAd()
                }
            }

            // Ödülün KESİNLİKLE sadece izlendikten sonra verilmesini sağlayan kısım:
            mRewardedAd?.show(this, object : OnUserEarnedRewardListener {
                override fun onUserEarnedReward(rewardItem: RewardItem) {
                    // *** YALNIZCA BU BLOK ÇALIŞIRSA ÖDÜLÜ VERİN! ***
                    gameView.grantLifeAndShowResumeDialog()
                }
            })

        } else {
            // Reklam yüklü değilse/yoksa: Ödül verilmez, kullanıcı bilgilendirilir.
            loadRewardedAd() // Tekrar yüklemeyi dene
        }
    }

    private fun loadRewardedAd() {
        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(this, "ca-app-pub-2120666198065087/9415118995", adRequest, object : RewardedAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                mRewardedAd = null
            }

            override fun onAdLoaded(rewardedAd: RewardedAd) {
                mRewardedAd = rewardedAd
            }
        })
    }

    override fun onPause() {
        super.onPause()
        gameView.pause()
        adViewBottom.pause()
    }

    override fun onResume() {
        super.onResume()
        gameView.resume()
        adViewBottom.resume()
    }

    override fun onDestroy() {
        super.onDestroy()
        adViewBottom.destroy()
    }

    fun saveHighScore(score: Int) {
        val sharedPref = getSharedPreferences("JumpHeroPrefs", Context.MODE_PRIVATE)
        val currentHighScore = sharedPref.getInt("high_score", 0)

        if (score > currentHighScore) {
            with(sharedPref.edit()) {
                putInt("high_score", score)
                apply()
            }
        }
    }
}