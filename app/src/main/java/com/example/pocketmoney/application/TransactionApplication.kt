package com.example.pocketmoney.application

import android.app.Application
import com.example.pocketmoney.database.TransactionRepository
import com.example.pocketmoney.database.TransactionRoomDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class TransactionApplication : Application() {
    val applicationScope = CoroutineScope(SupervisorJob())

    val database by lazy { TransactionRoomDatabase.getDatabase(this) }
    val repository by lazy { TransactionRepository(database.transactionDao()) }
}