package com.aghourservices.utils.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import com.aghourservices.R
import com.aghourservices.utils.helper.ProgressDialog.showThanksDialog
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback

class RewardAd {
    private var rewardedAd: RewardedInterstitialAd? = null
    private val tag = "Reward Ad"

    fun loadRewardedAd(context: Context) {
        val adRequest = AdRequest.Builder().build()
        val unitId = context.getString(R.string.ad_rewarded_unit_id)

        RewardedInterstitialAd.load(
            context,
            unitId,
            adRequest,
            object : RewardedInterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d(tag, adError.message)
                    Log.d(tag, unitId)
                    rewardedAd = null
                }

                override fun onAdLoaded(rewardedAd: RewardedInterstitialAd) {
                    Log.d(tag, "onAdLoaded...")
                    this@RewardAd.rewardedAd = rewardedAd
                }
            })
    }

    fun showAd(context: Context) {
        rewardedAd?.show(context as Activity) {
            showThanksDialog(context)
        }
    }
}