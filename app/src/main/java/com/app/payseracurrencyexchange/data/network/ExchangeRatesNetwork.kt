package com.app.payseracurrencyexchange.data.network

import com.app.payseracurrencyexchange.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ExchangeRatesNetwork {

    var okHttpClient = OkHttpClient()
    var httpLoggingInterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

    /* HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
     loggingInterceptor.setLevel(LoggingInterceptor.Level.BODY);
     OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
     .addInterceptor(loggingInterceptor)
     .build();*/

    val retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient.newBuilder().addInterceptor(httpLoggingInterceptor).build())
        .build()
        .create(ExchangeRatesApi::class.java)

}