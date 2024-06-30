package com.example.pocketmoney.database

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.Flow

class TransactionRepository(private val transactionDao: TransactionDao) {
    val allTransactions: Flow<List<Transaction>> = transactionDao.getAllTransactions()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(transaction: Transaction) {
        transactionDao.insert(transaction)
    }
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun delete(transaction: Transaction) {
        transactionDao.delete(transaction)
    }
    fun searchTransactions(query: String): LiveData<List<Transaction>> {
        return transactionDao.searchTransactions("%$query%")
    }
    fun getTransactionByName(searchString: String): Flow<List<Transaction>> {
        return transactionDao.getTransactionByName("%$searchString%")
    }
}