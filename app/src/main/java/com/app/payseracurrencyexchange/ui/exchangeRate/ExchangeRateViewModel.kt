package com.app.payseracurrencyexchange.ui.exchangeRate

import android.app.Application
import androidx.databinding.ObservableDouble
import androidx.databinding.ObservableField
import androidx.databinding.ObservableLong
import androidx.lifecycle.*
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.payseracurrencyexchange.data.model.ConvertCurrencyResponse
import com.app.payseracurrencyexchange.data.repository.ExchangeRatesRepository
import com.app.payseracurrencyexchange.data.repository.RoomRepository
import com.app.payseracurrencyexchange.data.room.AccountsDatabase
import com.app.payseracurrencyexchange.data.room.AccountsEntity
import com.app.payseracurrencyexchange.ui.base.ExchangeRatesStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.ArrayList

/**
 * [ExchangeRateViewModel] class for accessing methods from network repository [ExchangeRatesRepository] and database repository [RoomRepository] using coroutines
 */
class ExchangeRateViewModel(private val exchangeRatesRepository: ExchangeRatesRepository ,  application : Application) : AndroidViewModel(application) {

    private val _convertedAmount = MutableLiveData<ConvertCurrencyResponse>()
    val convertedAmount: LiveData<ConvertCurrencyResponse>
        get() = _convertedAmount

    private val _symbolList = MutableLiveData<List<String>>()
    val symbolList : LiveData<List<String>> = _symbolList

    private val _userAccounts = MutableLiveData<List<AccountsEntity>>()
    val userAccounts : LiveData<List<AccountsEntity>> = _userAccounts

    private val _responseStatus = MutableLiveData<ExchangeRatesStatus>()
    val responseStatus : LiveData<ExchangeRatesStatus>
        get() = _responseStatus

    private var roomRepository: RoomRepository

    // initialize dao, repository and all events
    init {
        val dao = AccountsDatabase.getInstance(application).accountsDao()
        roomRepository = RoomRepository(dao)
    }

    var progressLiveData = MutableLiveData<Boolean>()
    val amount = ObservableDouble(0.0)
    val remainingBalance = ObservableLong(0L)
    val toCurrency = ObservableField("")
    val fromCurrency = ObservableField("")
    val commision = ObservableDouble(0.0)

   // var checkKey : Int = 0
    private val _checkKey = MutableLiveData<Int>()
    val checkKey : LiveData<Int>
        get() = _checkKey


    init {
        getSymbols()
        getAll()
    }

    fun getConvertAmount(toCurrency: String?, fromCurrency: String?, amount: Double){
       viewModelScope.launch {
           try {
               progressLiveData.value = true
               val response = exchangeRatesRepository.getConvertAmount(toCurrency, fromCurrency, amount)
               response.isSuccessful.let { isSuccess ->
                   if(isSuccess){
                       progressLiveData.value = false
                       _convertedAmount.postValue(response.body())
                   }
               }
           } catch (e: Exception) {
               progressLiveData.value = false
               _responseStatus.value = ExchangeRatesStatus.ERROR
           }
       }
    }

    fun validate(): Boolean {
        return (amount.get() > 0.0
                && amount.get() < remainingBalance.get()
                && remainingBalance.get() > 0
                && amount.get() <= remainingBalance.get()
                && toCurrency.get()!!.isNotEmpty()
                && fromCurrency.get()!!.isNotEmpty())
                && toCurrency.get() != fromCurrency.get()
    }

   private fun getSymbols(){
        viewModelScope.launch {
            try {
                progressLiveData.value = true

                val response = exchangeRatesRepository.getSymbols()
                response.isSuccessful.let { isSuccess ->
                    if(isSuccess){
                        progressLiveData.value = false
                        _responseStatus.value = ExchangeRatesStatus.SUCCESS
                        val keySet: Set<String> = response.body().let {
                            it?.symbols?.keys!!
                        }
                        val listOfKeys = ArrayList(keySet)
                        _symbolList.value = listOfKeys
                    }

                }
            }
            catch (e: Exception){

                progressLiveData.value = false
                _responseStatus.value = ExchangeRatesStatus.ERROR
            }
        }
    }


     fun insert(accounts: AccountsEntity) {
        viewModelScope.launch {
            roomRepository.insert(accounts)
        }
    }

    fun checkKey(key : String) : Flow<Int> {
      return roomRepository.checkKey(key)
    }


    fun updateSum(key : String, balance : Long){
        viewModelScope.launch {
            roomRepository.updateSum(key, balance)
        }
    }

    fun updateMinus(key : String, balance : Long){
        viewModelScope.launch {
            roomRepository.updateMinus(key, balance)
        }
    }

     fun getAccountsById(key : String?) : LiveData<AccountsEntity> {
           return roomRepository.getAccountsById(key)
    }

    fun getAll(){
        viewModelScope.launch {
           roomRepository.getAllAccounts().collect { item ->
               _userAccounts.postValue(item)
           }
        }

    }
}