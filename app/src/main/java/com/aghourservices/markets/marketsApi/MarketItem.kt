package com.aghourservices.markets.marketsApi

data class MarketItem(
    val albumId: Int,
    val id: Int,
    val thumbnailUrl: String,
    val title: String,
    val url: String
)