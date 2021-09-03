package com.aghourservices.ads

import android.content.Context
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds

class AghourAdManager {
    companion object {
        fun loadAd(context: Context, adView: AdView) {
            MobileAds.initialize(context) {}
            val adRequest = AdRequest.Builder().build()
            adView.loadAd(adRequest)
        }
    }
}