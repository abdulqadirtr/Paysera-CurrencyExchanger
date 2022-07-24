package com.app.payseracurrencyexchange.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "account_table")
data class AccountsEntity(
    @PrimaryKey
    var accountName : String,
    var accountBalance : Double
)