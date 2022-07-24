package com.app.payseracurrencyexchange.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [AccountsEntity::class],  version = 1)
abstract class AccountsDatabase  : RoomDatabase(){

        abstract fun accountsDao() : AccountsDao

        companion object {

            @Volatile
            private var instance: AccountsDatabase? = null


            fun getInstance(ctx: Context): AccountsDatabase {
                return when (val temp = instance) {
                    null -> synchronized(this) {
                        Room.databaseBuilder(
                            ctx.applicationContext, AccountsDatabase::class.java,
                            "accounts_database"
                        )
                            .fallbackToDestructiveMigration()
                            .build()
                    }
                    else -> temp
                }
            }
        }
    }