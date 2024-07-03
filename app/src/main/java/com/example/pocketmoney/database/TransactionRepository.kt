package com.example.pocketmoney.database

import androidx.annotation.WorkerThread
import androidx.lifecycle.asFlow
import com.example.pocketmoney.utils.CustomFlowCombiner
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class TransactionRepository(
    private val expenseDao: ExpenseDao,
    private val incomeDao: IncomeDao,
    private val flowCombiner: CustomFlowCombiner
) {

    val allExpenses: Flow<List<Expense>> = expenseDao.getAllExpenses()
    val allIncomes: Flow<List<Income>> = incomeDao.getAllIncomes()

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
