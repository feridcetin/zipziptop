package com.feridcetin.zipziptop

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    // Splash ekranının gösterileceği süre (milisaniye)
    private val SPLASH_TIME_OUT: Long = 2000 // 2 saniye

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Splash ekranınızın layout'unu burada ayarlayabilirsiniz.
         setContentView(R.layout.activity_splash)

        // UYGULAMA POLİTİKASINA UYUM İÇİN:
        // Burada MobileAds.initialize() veya reklam yükleme/gösterme kodu KESİNLİKLE YOKTUR.

        Handler(Looper.getMainLooper()).postDelayed({
            // Belirtilen süre dolduktan sonra MainActivity'yi başlat
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)

            // Geri tuşuna basıldığında tekrar gelmemesi için bu aktiviteyi sonlandır
            finish()
        }, SPLASH_TIME_OUT)
    }
}