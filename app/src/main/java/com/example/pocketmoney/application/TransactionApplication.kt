package com.example.pocketmoney.application

import android.app.Application
import com.example.pocketmoney.database.ExpenseDao
import com.example.pocketmoney.database.IncomeDao
import com.example.pocketmoney.database.PocketMoneyDatabase
import com.example.pocketmoney.database.TransactionRepository
import com.example.pocketmoney.utils.CustomFlowCombiner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class TransactionApplication : Application() {
    val applicationScope = CoroutineScope(SupervisorJob())

    // Access both ExpenseDao and IncomeDao
    private val database by lazy { PocketMoneyDatabase.getDatabase(this) }
    private val expenseDao: ExpenseDao by lazy { database.expenseDao() }
    private val incomeDao: IncomeDao by lazy { database.incomeDao() }

    val repository by lazy { TransactionRepository(expenseDao, incomeDao, CustomFlowCombiner()) }
}
