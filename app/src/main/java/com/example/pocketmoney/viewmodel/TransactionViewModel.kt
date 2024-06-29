package com.example.pocketmoney.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.pocketmoney.database.Transaction
import com.example.pocketmoney.database.TransactionRepository
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.launch


class TransactionViewModel(private val repository: TransactionRepository) : ViewModel() {

    val allStudent: LiveData<List<Transaction>> = repository.allTransactions.asLiveData()
    fun insert(transaction: Transaction) = viewModelScope.launch {
        repository.insert(transaction)
    }
    fun getStudentByData(searchString: String): LiveData<List<Transaction>> {
        return repository.getTransactionByName(searchString).asLiveData()
    }
    fun delete(transaction: Transaction) = viewModelScope.launch {
        repository.delete(transaction)
    }
}

class TransactionViewModelFactory(private val repository: TransactionRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransactionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TransactionViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
