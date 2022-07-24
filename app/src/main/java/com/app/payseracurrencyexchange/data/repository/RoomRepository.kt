package com.app.payseracurrencyexchange.data.repository

import androidx.lifecycle.LiveData
import com.app.payseracurrencyexchange.data.room.AccountsDao
import com.app.payseracurrencyexchange.data.room.AccountsEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

    class RoomRepository(private val accountsDao: AccountsDao){


        suspend fun insert(accounts: AccountsEntity) {
            withContext(Dispatchers.IO) {
                // Dispatchers.IO (main-safety block)
                /* perform network IO here */
                // Dispatchers.IO (main-safety block)
                accountsDao.insert(accounts)
            }

        }

         fun checkKey(key : String) : Flow<Int>{
            return accountsDao.containsPrimaryKey(key)
        }

        fun update(accounts: AccountsEntity) {
            accountsDao.update(accounts)
        }

        fun getAllAccounts(): Flow<List<AccountsEntity>> {
            return accountsDao.getAllAccounts()
        }

         fun getAccountsById(key : String?) : LiveData<AccountsEntity> {
            return accountsDao.getAccountById(key)
        }

        suspend fun updateSum(key : String, balance : Long) {
            accountsDao.updateSum(key, balance)
        }

        suspend fun updateMinus(key : String, balance : Double) {
            accountsDao.updateMinus(key, balance)
        }
    }