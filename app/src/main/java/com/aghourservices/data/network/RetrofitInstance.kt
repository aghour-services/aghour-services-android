package com.aghourservices.data.network

import com.aghourservices.BuildConfig
import com.aghourservices.data.api.ArticlesApi
import com.aghourservices.data.api.CategoriesApi
import com.aghourservices.data.api.CommentsApi
import com.aghourservices.data.api.FirmsApi
import com.aghourservices.data.api.LikeApi
import com.aghourservices.data.api.NotificationsApi
import com.aghourservices.data.api.SearchApi
import com.aghourservices.data.api.TagApi
import com.aghourservices.data.api.UserApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitInstance {

    private val retrofit: Retrofit by lazy {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BASIC)

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .callTimeout(1, TimeUnit.MINUTES)
            .connectTimeout(1, TimeUnit.MINUTES)
            .readTimeout(1, TimeUnit.MINUTES)
            .writeTimeout(1, TimeUnit.MINUTES)
            .retryOnConnectionFailure(true)
            .build()

        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BuildConfig.BASE_URL)
            .client(client)
            .build()
    }

    val categoriesApi: CategoriesApi by lazy {
        retrofit.create(CategoriesApi::class.java)
    }

    val firmsApi: FirmsApi by lazy {
        retrofit.create(FirmsApi::class.java)
    }

    val articlesApi: ArticlesApi by lazy {
        retrofit.create(ArticlesApi::class.java)
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

    val commentsApi: CommentsApi by lazy {
        retrofit.create(CommentsApi::class.java)
    }

    val likeApi: LikeApi by lazy {
        retrofit.create(LikeApi::class.java)
    }

    val notificationsApi: NotificationsApi by lazy {
        retrofit.create(NotificationsApi::class.java)
    }
}