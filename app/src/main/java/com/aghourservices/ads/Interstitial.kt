package com.aghourservices.ads

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.aghourservices.cache.Settings
import com.aghourservices.cache.UserInfo
import com.aghourservices.categories.CategoriesActivity
import com.aghourservices.user.SignUpActivity
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

class Interstitial {
    private var interstitialAd: InterstitialAd? = null
    private val tag = "Interstitial Ad"
    private val adUnit = "ca-app-pub-3940256099942544/1033173712"

    fun load(context: Context) {
        var adRequest = AdRequest.Builder().build()

        InterstitialAd.load(context, adUnit, adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d(tag, adError?.message.toString())
                    interstitialAd = null
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    Log.d(tag, "Ad was loaded.")
                    this@Interstitial.interstitialAd = interstitialAd
                    Toast.makeText(context, "سيظهر اعلان بعد 3 ثواني", Toast.LENGTH_SHORT).show()
                    Handler(Looper.getMainLooper()).postDelayed({
                        interstitialAd.show(context as Activity)
                    }, 3000)
                }
            })
    }
}