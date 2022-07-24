package com.app.payseracurrencyexchange.data.room

import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountsDao {
        /**
         * Insert new accounts into the database
         */
        @Insert(onConflict = OnConflictStrategy.REPLACE)
        suspend fun insert(accounts: AccountsEntity)

        @Update
        fun update(accounts: AccountsEntity)

        @Delete
        fun delete(accounts: AccountsEntity)

        @Query("SELECT * FROM account_table")
        fun getAllAccounts(): Flow<List<AccountsEntity>>

        @Query("delete from account_table")
        fun deleteAllComics(){}

        @Query("SELECT count(*) FROM account_table WHERE accountName = :uid ")
         fun containsPrimaryKey(uid: String): Flow<Int>

        @Query("UPDATE account_table SET accountBalance = accountBalance + :addValue WHERE accountName =:key")
        suspend fun updateSum(key : String, addValue: Long)

        @Query("UPDATE account_table SET accountBalance = accountBalance - :addValue WHERE accountName =:key")
        suspend fun updateMinus(key : String, addValue: Double)

        @Query("SELECT * FROM account_table WHERE accountName = :key")
        fun getAccountById(key: String?): LiveData<AccountsEntity>
}