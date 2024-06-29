package com.example.pocketmoney.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Transaction::class], version = 3, exportSchema = false)
@TypeConverters(Converter::class)

abstract class TransactionRoomDatabase:RoomDatabase() {
    //companion = static in java
    abstract fun transactionDao(): TransactionDao

    companion object{
        private var INSTANCE: TransactionRoomDatabase? = null   //singleton design pattern

        fun getDatabase(context : Context):TransactionRoomDatabase{
            return INSTANCE ?: synchronized(this) {    //synchronized block
                val instance = Room.databaseBuilder(context.applicationContext,
                    TransactionRoomDatabase::class.java,
                    "transaction_database")
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}