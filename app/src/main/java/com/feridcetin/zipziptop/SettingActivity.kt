package com.feridcetin.zipziptop

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.OnUserEarnedRewardListener // Yeni import

class SettingActivity : AppCompatActivity()  {

    private lateinit var sharedPref: SharedPreferences
    private var mRewardedAd: RewardedAd? = null

    private lateinit var btnLangEn: Button
    private lateinit var btnLangTr: Button
    private lateinit var switchPremiumCharacter: Switch
    private lateinit var switchAdvancedTheme: Switch
    private lateinit var btnSaveCharacter: Button
    private lateinit var btnMusicSettings: Button
    private lateinit var btnBackgroundSettings: Button

    private var selectedCharacterColor: Int = R.drawable.rounded_button_red
    private lateinit var characterButtons: List<ImageButton>

    private val colorDrawables = mapOf(
        R.id.btnRed to R.drawable.rounded_button_red,
        R.id.btnBlue to R.drawable.rounded_button_blue,
        R.id.btnGreen to R.drawable.rounded_button_green,
        R.id.btnYellow to R.drawable.rounded_button_yellow,
        R.id.btnOrange to R.drawable.rounded_button_orange,
        R.id.btnPink to R.drawable.rounded_button_pink,
        R.id.btnTurquoise to R.drawable.rounded_button_turquoise,
        R.id.btnWhite to R.drawable.rounded_button_white,
        R.id.btnBlack to R.drawable.rounded_button_black,
        R.id.btnBrown to R.drawable.rounded_button_brown,
        R.id.btnGray to R.drawable.rounded_button_gray,
        R.id.btnPurple to R.drawable.rounded_button_purple
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        MobileAds.initialize(this) {}
        loadRewardedAd()

        sharedPref = getSharedPreferences("JumpHeroPrefs", Context.MODE_PRIVATE)

        initializeViews()
        loadSettings()
        setupListeners()
    }

    private fun initializeViews() {
        btnLangEn = findViewById(R.id.btn_lang_en)
        btnLangTr = findViewById(R.id.btn_lang_tr)
        switchPremiumCharacter = findViewById(R.id.switch_premium_character)
        switchAdvancedTheme = findViewById(R.id.switch_advanced_theme)
        btnSaveCharacter = findViewById(R.id.btnSaveCharacter)
        btnMusicSettings = findViewById(R.id.btnMusicSettings)
        btnBackgroundSettings = findViewById(R.id.btn_background_settings)

        characterButtons = listOf(
            findViewById(R.id.btnRed),
            findViewById(R.id.btnBlue),
            findViewById(R.id.btnGreen),
            findViewById(R.id.btnYellow),
            findViewById(R.id.btnOrange),
            findViewById(R.id.btnPink),
            findViewById(R.id.btnTurquoise),
            findViewById(R.id.btnWhite),
            findViewById(R.id.btnBlack),
            findViewById(R.id.btnBrown),
            findViewById(R.id.btnGray),
            findViewById(R.id.btnPurple)
        )
    }

    private fun loadSettings() {
        updateLanguageButtons()
        switchPremiumCharacter.isChecked = sharedPref.getBoolean("hasCharactersPack", false)
        switchAdvancedTheme.isChecked = sharedPref.getBoolean("hasAdvancedTheme", false)
        selectedCharacterColor = sharedPref.getInt("selected_character_color", R.drawable.rounded_button_red)
        updateCharacterSelectionUI()
        /*Log.e("this", "hasCharactersPack = ${sharedPref.getBoolean("hasCharactersPack", false)} " +
                                                 "hasAdvancedTheme = ${sharedPref.getBoolean("hasAdvancedTheme", false)} " +
                                                 "selected_character_color = ${ sharedPref.getInt("selected_character_color", R.drawable.rounded_button_red)} " )*/
    }

    private fun setupListeners() {
        btnLangEn.setOnClickListener {
            saveStringSetting("language", "en")
            LocaleHelper.setLocaleAndRestart(this, "en")
            updateLanguageButtons()
        }
        btnLangTr.setOnClickListener {
            saveStringSetting("language", "tr")
            LocaleHelper.setLocaleAndRestart(this, "tr")
            updateLanguageButtons()
        }

        switchPremiumCharacter.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                if (!sharedPref.getBoolean("hasCharactersPack", false)) {
                }
            } else {
                switchPremiumCharacter.isChecked = true
            }
        }

        switchAdvancedTheme.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                if (!sharedPref.getBoolean("hasAdvancedTheme", false)) {
                }
            } else {
                switchAdvancedTheme.isChecked = true
            }
        }

        characterButtons.forEach { button ->
            button.setOnClickListener {
                selectedCharacterColor = colorDrawables[it.id] ?: R.drawable.rounded_button_red
                updateCharacterSelectionUI()
            }
        }

        btnSaveCharacter.setOnClickListener {
            showRewardedAd()
        }

        btnMusicSettings.setOnClickListener {
            val intent = Intent(this, MusicSettingActivity::class.java)
            startActivity(intent)
        }

        btnBackgroundSettings.setOnClickListener {
            val intent = Intent(this, BackgroundSettingActivity::class.java)
            startActivity(intent)
        }
    }

    private fun updateLanguageButtons() {
        val savedLanguage = sharedPref.getString("language", "en")
        btnLangEn.setBackgroundColor(if (savedLanguage == "en") Color.parseColor("#4CAF50") else Color.LTGRAY)
        btnLangTr.setBackgroundColor(if (savedLanguage == "tr") Color.parseColor("#4CAF50") else Color.LTGRAY)
    }

    private fun updateCharacterSelectionUI() {
        val selectedBorderDrawable = ContextCompat.getDrawable(this, R.drawable.rounded_button_border)

        characterButtons.forEach { button ->
            val colorDrawableId = colorDrawables[button.id]
            if (colorDrawableId != null) {
                if (colorDrawableId == selectedCharacterColor) {
                    val colorDrawable = ContextCompat.getDrawable(this, colorDrawableId)
                    val layers = arrayOf(colorDrawable, selectedBorderDrawable)
                    val layerDrawable = LayerDrawable(layers)
                    button.background = layerDrawable
                } else {
                    button.background = ContextCompat.getDrawable(this, colorDrawableId)
                }
            }
        }
    }

    private fun saveBooleanSetting(key: String, value: Boolean) {
        with(sharedPref.edit()) {
            putBoolean(key, value)
            apply()
        }
    }

    private fun saveStringSetting(key: String, value: String) {
        with(sharedPref.edit()) {
            putString(key, value)
            apply()
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

    /*
    private fun showRewardedAd() {
        if (mRewardedAd != null) {
            mRewardedAd?.show(this) {
                saveCharacterSelection()
                Toast.makeText(this, R.string.showRewardedAd, Toast.LENGTH_LONG).show()
                finish()
            }
        } else {
            Toast.makeText(this,R.string.showRewardedAdElse, Toast.LENGTH_SHORT).show()
        }
    }*/

    private fun showRewardedAd() {
        if (mRewardedAd != null) {

            mRewardedAd?.fullScreenContentCallback = object : FullScreenContentCallback() {

                // Reklam ekrandan kaldırıldığında (kullanıcı kapattığında)
                override fun onAdDismissedFullScreenContent() {
                    // Bu noktada sadece reklam kapatıldı, ödül verilmiş de olabilir, verilmemiş de.
                    // Ödül mantığı zaten aşağıda halledildi.
                    Log.d("RewardedAd", "Reklam kapatıldı.")
                    loadRewardedAd() // Yeni bir reklam yükle
                }

                // Reklam gösterilemediğinde
                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    Log.e("RewardedAd", "Reklam gösterilemedi: " + adError.message)
                    Toast.makeText(this@SettingActivity, getString(R.string.showRewardedAdElse), Toast.LENGTH_SHORT).show()
                    mRewardedAd = null // Reklamı sıfırla
                    loadRewardedAd() // Yeni reklam yükle
                }

                // Reklam gösterildiğinde
                override fun onAdShowedFullScreenContent() {
                    Log.d("RewardedAd", "Reklam gösterildi.")
                }
            }

            // Ödülü sadece kullanıcı izleyerek kazandığında ver!
            mRewardedAd?.show(this) {
                // SADECE VE SADECE BU BLOK ÇALIŞIRSA ÖDÜL VERİLİR (yani reklam başarıyla izlenmiştir)
                saveCharacterSelection() // Karakteri KESİNLİKLE BURADA KAYDET!
                Toast.makeText(this, R.string.showRewardedAd, Toast.LENGTH_LONG).show()
                finish() // Ayarlar ekranını kapat.
            }

        } else {
            // Reklam yüklenmediyse: Ödül verilmez ve uyarı gösterilir.
            Toast.makeText(this,R.string.showRewardedAdElse, Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveCharacterSelection() {
        with(sharedPref.edit()) {
            putInt("selected_character_color", selectedCharacterColor)
            apply()
        }
    }

}