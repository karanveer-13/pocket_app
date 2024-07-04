package com.example.pocketmoney.database

import androidx.annotation.WorkerThread
import androidx.lifecycle.asFlow
import com.example.pocketmoney.utils.CustomFlowCombiner
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged

class TransactionRepository(
    private val expenseDao: ExpenseDao,
    private val incomeDao: IncomeDao,
    private val categoryDao: CategoryDao,
    private val flowCombiner: CustomFlowCombiner
) {

    val allExpenses: Flow<List<Expense>> = expenseDao.getAllExpenses()
    val allIncomes: Flow<List<Income>> = incomeDao.getAllIncomes()
    private val _allCategoriesFlow: Flow<List<Category>> = categoryDao.getAllCategories()
        .distinctUntilChanged()

    fun getAllCategories(): Flow<List<Category>> = _allCategoriesFlow

    fun getExpenses(): Flow<List<Expense>> = allExpenses

    fun getIncomes(): Flow<List<Income>> = allIncomes

    fun getExpensesByCategory(categoryId: Int): Flow<List<Expense>> {
        return expenseDao.getExpensesByCategory(categoryId)
    }

    fun getIncomesByCategory(categoryId: Int): Flow<List<Income>> {
        return incomeDao.getIncomesByCategory(categoryId)
    }

    fun getAllTransactionsCombined(): Flow<List<Any>> {
        return combine(
            allExpenses,
            allIncomes
        ) { expenses, incomes ->
            val transactions = mutableListOf<Any>()
            transactions.addAll(expenses)
            transactions.addAll(incomes)
            transactions
        }
    }

    fun getTransactionsByCategory(categoryId: Int): Flow<List<Any>> {
        val expenseFlow = getExpensesByCategory(categoryId)
        val incomeFlow = getIncomesByCategory(categoryId)

        return flowCombiner.combine(
            expenseFlow,
            incomeFlow
        ) { expenses, incomes ->
            val transactions = mutableListOf<Any>()
            transactions.addAll(expenses)
            transactions.addAll(incomes)
            transactions
        }
    }



    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(transaction: Any) {
        when (transaction) {
            is Expense -> expenseDao.insert(transaction)
            is Income -> incomeDao.insert(transaction)
            else -> throw IllegalArgumentException("Unknown transaction type")
        }
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun delete(transaction: Any) {
        when (transaction) {
            is Expense -> expenseDao.delete(transaction)
            is Income -> incomeDao.delete(transaction)
            else -> throw IllegalArgumentException("Unknown transaction type")
        }
    }

    fun searchTransactions(query: String): Flow<List<Any>> {
        val expenseFlow = expenseDao.searchExpense("%$query%").asFlow()
        val incomeFlow = incomeDao.searchIncome("%$query%").asFlow()

        return flowCombiner.combine(
            expenseFlow,
            incomeFlow
        ) { expenses, incomes ->
            val transactions = mutableListOf<Any>()
            transactions.addAll(expenses)
            transactions.addAll(incomes)
            transactions
        }
    }

}
