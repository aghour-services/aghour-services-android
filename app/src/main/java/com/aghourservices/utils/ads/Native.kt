package com.aghourservices.utils.ads

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.aghourservices.R
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView

class Native {
    companion object {
        fun load(context: Context, parent: ViewGroup) {
            val unitId = context.getString(R.string.ad_native_unit_id)
            val adLoader = AdLoader.Builder(context, unitId)
                .forNativeAd { ad: NativeAd ->
                    display(context, parent, ad)
                }.withNativeAdOptions(NativeAdOptions.Builder().build()).build()
            adLoader.loadAd(AdRequest.Builder().build())
        }

        @SuppressLint("InflateParams")
        private fun display(context: Context, parent: ViewGroup, ad: NativeAd) {
            val inflater = context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val adView = inflater.inflate(R.layout.native_ad_layout, null) as NativeAdView

            val headlineView = adView.findViewById<TextView>(R.id.ad_headline)
            headlineView.text = ad.headline
            adView.headlineView = headlineView
            adView.mediaView = adView.findViewById(R.id.ad_media)
            adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
            adView.iconView = adView.findViewById(R.id.ad_app_icon)
            adView.priceView = adView.findViewById(R.id.ad_price)
            adView.starRatingView = adView.findViewById(R.id.ad_stars)
            adView.storeView = adView.findViewById(R.id.ad_store)
            adView.advertiserView = adView.findViewById(R.id.ad_advertiser)

            if (ad.price == null) {
                adView.priceView!!.visibility = View.INVISIBLE
            } else {
                adView.priceView!!.visibility = View.VISIBLE
                (adView.priceView as TextView).text = ad.price
            }

            if (ad.callToAction == null) {
                adView.callToActionView!!.visibility = View.INVISIBLE
            } else {
                adView.callToActionView!!.visibility = View.VISIBLE
                (adView.callToActionView as Button).text = ad.callToAction
            }
            
            adView.setNativeAd(ad)
            parent.removeAllViews()
            parent.addView(adView)
        }
    }
}