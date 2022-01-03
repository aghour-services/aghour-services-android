package com.aghourservices.ads

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.aghourservices.R
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

class Interstitial {
    private var interstitialAd: InterstitialAd? = null
    private val tag = "Interstitial Ad"

    fun load(context: Context) {
        val adRequest = AdRequest.Builder().build()
        val unitId = context.getString(R.string.ad_interstitial_unit_id)

        InterstitialAd.load(context, unitId, adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d(tag, adError.message.toString())
                    interstitialAd = null
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    Log.d(tag, "Ad was loaded.")
                    this@Interstitial.interstitialAd = interstitialAd
                    Handler(Looper.getMainLooper()).postDelayed({
                        interstitialAd.show(context as Activity)
                    }, 30000)
                    Handler(Looper.getMainLooper()).postDelayed({
                        interstitialAd.show(context as Activity)
                    }, 60000)
                }
            })
    }
}