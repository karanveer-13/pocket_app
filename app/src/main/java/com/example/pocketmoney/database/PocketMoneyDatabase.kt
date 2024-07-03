package com.example.pocketmoney.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Income::class, Expense::class], version = 4, exportSchema = false)
@TypeConverters(Converter::class)
abstract class PocketMoneyDatabase : RoomDatabase() {
    abstract fun incomeDao(): IncomeDao
    abstract fun expenseDao(): ExpenseDao

    companion object {
        @Volatile
        private var INSTANCE: PocketMoneyDatabase? = null

        fun getDatabase(context: Context): PocketMoneyDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PocketMoneyDatabase::class.java,
                    "pocketmoney_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
