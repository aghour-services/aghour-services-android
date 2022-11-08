package com.aghourservices.utils.ads

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.aghourservices.R
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

class RewardAd {
    private var rewardedAd: RewardedAd? = null
    private val tag = "Reward Ad"

    fun loadRewardedAd(context: Context) {
        val adRequest = AdRequest.Builder().build()
        val unitId = context.getString(R.string.ad_rewarded_unit_id)

        RewardedAd.load(context, unitId, adRequest, object : RewardedAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.d(tag, adError.message)
                rewardedAd = null
            }

            override fun onAdLoaded(rewardedAd: RewardedAd) {
                Log.d(tag, "onAdLoaded...")
                this@RewardAd.rewardedAd = rewardedAd
                Toast.makeText(context, "سيظهر اعلان بعد 5 ثواني", Toast.LENGTH_SHORT).show()
                Handler(Looper.getMainLooper()).postDelayed({
                    rewardedAd.show(context as Activity) {
                        Toast.makeText(context, "شكرا لك على دعم التطبيق", Toast.LENGTH_SHORT)
                            .show()
                    }
                }, 5000)
            }
        })
    }
}