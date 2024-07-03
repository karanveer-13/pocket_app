package com.example.pocketmoney.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

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
                    .addCallback(PocketMoneyDatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class PocketMoneyDatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateDatabase(database.incomeDao())
                    }
                }
            }
        }

        suspend fun populateDatabase(incomeDao: IncomeDao) {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val dummyIncomes = listOf(
                Income(0, "Salary", 5000.0, dateFormat.parse("2024-07-01") ?: Date()),
                Income(0, "Freelancing", 1500.0, dateFormat.parse("2024-07-02") ?: Date()),
                Income(0, "Interest", 200.0, dateFormat.parse("2024-07-03") ?: Date())
            )
            for (income in dummyIncomes) {
                incomeDao.insert(income)
            }
        }
    }
}
