package com.example.pocketmoney.application

import android.app.Application
import com.example.pocketmoney.database.Category
import com.example.pocketmoney.database.CategoryDao
import com.example.pocketmoney.database.ExpenseDao
import com.example.pocketmoney.database.IncomeDao
import com.example.pocketmoney.database.PocketMoneyDatabase
import com.example.pocketmoney.database.TransactionRepository
import com.example.pocketmoney.utils.CustomFlowCombiner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class TransactionApplication : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob())

    // Access both ExpenseDao and IncomeDao
    private lateinit var database: PocketMoneyDatabase
    private lateinit var expenseDao: ExpenseDao
    private lateinit var incomeDao: IncomeDao
    private lateinit var categoryDao: CategoryDao

    lateinit var repository: TransactionRepository
        private set

    override fun onCreate() {
        super.onCreate()
        database = PocketMoneyDatabase.getDatabase(this)
        expenseDao = database.expenseDao()
        incomeDao = database.incomeDao()
        categoryDao = database.categoryDao()

        repository = TransactionRepository(expenseDao, incomeDao, categoryDao, CustomFlowCombiner())

        // Insert initial categories if they don't exist
        insertInitialCategories()
    }

    private fun insertInitialCategories() {
        runBlocking {
            val categories = categoryDao.getAllCategories().first()
            if (categories.size!=2) {
                categoryDao.insert(Category(1, "Income"))
                categoryDao.insert(Category(2, "Expense"))
            }
        }
    }
}
