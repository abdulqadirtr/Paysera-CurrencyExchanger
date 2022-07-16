package com.app.payseracurrencyexchange.data.repository

import com.app.payseracurrencyexchange.data.network.ExchangeRatesNetwork

class ExchangeRatesRepository {

    suspend fun getConvertAmount(toCurrency : String, fromCurrency : String, amount : Double) =
        ExchangeRatesNetwork.retrofit.getConvertAmount(toCurrency, fromCurrency, amount)
}