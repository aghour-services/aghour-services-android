package com.aghourservices.ads

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.aghourservices.R
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView

class AghourAdManager {
    companion object {
        fun displayBannerAd(context: Context, adView: AdView) {
            MobileAds.initialize(context) {}
            val adRequest = AdRequest.Builder().build()
            adView.loadAd(adRequest)
        }

        fun loadNativeAd(context: Context, parent: ViewGroup) {
            val unitId = context.getString(R.string.ad_native_unit_id)
            val adLoader = AdLoader.Builder(context, unitId)
                .forNativeAd { ad: NativeAd ->
                    displayNativeAd(context, parent, ad)
                }.withNativeAdOptions(NativeAdOptions.Builder().build()).build()

            adLoader.loadAd(AdRequest.Builder().build())
        }

        private fun displayNativeAd(context: Context, parent: ViewGroup, ad: NativeAd) {
            val inflater = context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val adView = inflater.inflate(R.layout.native_ad_layout, null) as NativeAdView

            val headlineView = adView.findViewById<TextView>(R.id.ad_headline)
            headlineView.text = ad.headline
            adView.headlineView = headlineView
            adView.mediaView = adView.findViewById<MediaView>(R.id.ad_media_view)
            adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)

            if (ad.callToAction == null) {
                adView.callToActionView.visibility = View.INVISIBLE
            } else {
                adView.callToActionView.visibility = View.VISIBLE
                (adView.callToActionView as Button).text = ad.callToAction
            }

            adView.setNativeAd(ad)
            parent.removeAllViews()
            parent.addView(adView)
        }
    }
}