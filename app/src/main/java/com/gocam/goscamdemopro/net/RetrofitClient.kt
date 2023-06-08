package com.gocam.goscamdemopro.net

import com.gos.platform.api.ConfigManager
import com.gos.platform.api.PlatformApiTask
import com.gos.platform.device.util.LogUtil
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.*
import java.lang.Exception
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLException

object RetrofitClient {

    private val mApiService: ApiService
    private val lock = Any()
    val apiService: ApiService
        get() {
            synchronized(lock) {
                return mApiService
            }
        }

    init {
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .build()

        val retrofit = Retrofit.Builder()
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(ConfigManager.getUrl())
            .build()
        mApiService = retrofit.create(ApiService::class.java)
    }

}