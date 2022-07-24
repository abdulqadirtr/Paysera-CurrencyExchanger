package com.app.payseracurrencyexchange.data.network

import com.app.payseracurrencyexchange.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ExchangeRatesNetwork {

    var httpLoggingInterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

   private val requestInterceptor = Interceptor { chain ->
        val url = chain.request()
            .url
            .newBuilder()
            .addQueryParameter("apikey", BuildConfig.API_KEY)
            .build()
        val request = chain.request()
            .newBuilder()
            .url(url)
            .build()

        return@Interceptor chain.proceed(request)

    }

   private val okhttp = OkHttpClient.Builder().addInterceptor(requestInterceptor)
        .addNetworkInterceptor(httpLoggingInterceptor).readTimeout(60, TimeUnit.SECONDS).build()

    val retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(okhttp)
        .build()
        .create(ExchangeRatesApi::class.java)

}