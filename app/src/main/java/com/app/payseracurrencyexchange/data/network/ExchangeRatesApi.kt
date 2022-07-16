package com.app.payseracurrencyexchange.data.network

import com.app.payseracurrencyexchange.data.model.ConvertCurrencyResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ExchangeRatesApi {

    @GET("convert")
    suspend fun getConvertAmount(@Query("to") toCurrency: String,
                                 @Query("from") fromCurrency: String,
                                 @Query("amount") amount: Double): Response<ConvertCurrencyResponse>

    @GET("latest")
    suspend fun getLatest(): Response<ConvertCurrencyResponse>

}