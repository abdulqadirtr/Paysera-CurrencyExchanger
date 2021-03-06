package com.app.payseracurrencyexchange.ui.base

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.app.payseracurrencyexchange.data.repository.ExchangeRatesRepository
import com.app.payseracurrencyexchange.ui.exchangeRate.ExchangeRateViewModel

class ViewModelFactory (private val exchangeRateRepo: ExchangeRatesRepository, private  val application: Application): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(ExchangeRateViewModel::class.java)) {
            ExchangeRateViewModel(this.exchangeRateRepo, application) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}