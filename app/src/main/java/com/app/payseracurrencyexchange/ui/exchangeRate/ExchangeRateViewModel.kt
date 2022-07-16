package com.app.payseracurrencyexchange.ui.exchangeRate

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.payseracurrencyexchange.data.model.ConvertCurrencyResponse
import com.app.payseracurrencyexchange.data.repository.ExchangeRatesRepository
import kotlinx.coroutines.launch
import java.lang.Exception

class ExchangeRateViewModel(private val exchangeRatesRepository: ExchangeRatesRepository) : ViewModel() {

    private val _convertedAmount = MutableLiveData<ConvertCurrencyResponse>()

    val convertedAmount: LiveData<ConvertCurrencyResponse>
        get() = _convertedAmount

    fun getConvertAmount(){
       viewModelScope.launch {
           try {
               val response = exchangeRatesRepository.getConvertAmount("USD", "EUR", 12.0)
               response.isSuccessful.let { isSuccess ->
                   if(isSuccess){
                       _convertedAmount.value = response.body()
                   }

               }
           } catch (e: Exception) {

           }

       }
    }
}