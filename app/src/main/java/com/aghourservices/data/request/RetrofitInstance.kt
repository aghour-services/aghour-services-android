package com.aghourservices.data.request

import android.content.Context
import com.aghourservices.R
import com.aghourservices.data.api.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitInstance(context: Context) {
    private val baseUrl = context.getString(R.string.base_url)

    private val retrofit: Retrofit by lazy {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BASIC)

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .callTimeout(1, TimeUnit.MINUTES)
            .connectTimeout(1, TimeUnit.MINUTES)
            .build()

        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(baseUrl)
            .client(client)
            .build()
    }

    val categoriesApi: CategoriesApi by lazy {
        retrofit.create(CategoriesApi::class.java)
    }

    val firmsApi: FirmsApi by lazy {
        retrofit.create(FirmsApi::class.java)
    }

    val newsApi: NewsApi by lazy {
        retrofit.create(NewsApi::class.java)
    }

    val searchApi: SearchApi by lazy {
        retrofit.create(SearchApi::class.java)
    }

    val userApi: UserApi by lazy {
        retrofit.create(UserApi::class.java)
    }

    val tagsApi: TagApi by lazy {
        retrofit.create(TagApi::class.java)
    }
}