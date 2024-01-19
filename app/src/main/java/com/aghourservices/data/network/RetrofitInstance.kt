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
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitInstance {
    private val client = OkHttpClient.Builder()
        .callTimeout(30, TimeUnit.SECONDS)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(1, TimeUnit.MINUTES)
        .retryOnConnectionFailure(true)
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BuildConfig.BASE_URL)
        .client(client)
        .build()

    val categoriesApi: CategoriesApi = retrofit.create(CategoriesApi::class.java)
    val firmsApi: FirmsApi = retrofit.create(FirmsApi::class.java)
    val articlesApi: ArticlesApi = retrofit.create(ArticlesApi::class.java)
    val searchApi: SearchApi = retrofit.create(SearchApi::class.java)
    val userApi: UserApi = retrofit.create(UserApi::class.java)
    val tagsApi: TagApi = retrofit.create(TagApi::class.java)
    val commentsApi: CommentsApi = retrofit.create(CommentsApi::class.java)
    val likeApi: LikeApi = retrofit.create(LikeApi::class.java)
    val notificationsApi: NotificationsApi = retrofit.create(NotificationsApi::class.java)
}