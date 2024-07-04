package com.example.pocketmoney.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Database(entities = [Expense::class, Income::class, Category::class], version = 5, exportSchema = false)
@TypeConverters(Converter::class)
abstract class PocketMoneyDatabase : RoomDatabase() {

    abstract fun incomeDao(): IncomeDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun categoryDao(): CategoryDao

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
                    .fallbackToDestructiveMigration() // Fallback in case migrations fail
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            GlobalScope.launch(Dispatchers.IO) {
                                val categoryDao = getDatabase(context).categoryDao()
                                categoryDao.insert(Category(0, "Income"))
                                categoryDao.insert(Category(1, "Expense"))
                            }
                        }
                    }).build()
                INSTANCE = instance
                instance

            }

        }
    }


}
