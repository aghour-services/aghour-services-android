package com.aghourservices.markets.marketsApi

import retrofit2.http.GET

interface ApiServices {
    @GET("photos")
    fun loadMarketsList(): retrofit2.Call<ArrayList<MarketItem>>
}