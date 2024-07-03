package com.example.pocketmoney.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.pocketmoney.database.Expense
import com.example.pocketmoney.database.Income
import com.example.pocketmoney.database.TransactionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TransactionViewModel(private val repository: TransactionRepository) : ViewModel() {

    val allTransactions: LiveData<List<Any>> = repository.getAllTransactionsCombined()
        .asLiveData()

    fun insert(transaction: Any) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insert(transaction)
        }
    }

    fun delete(transaction: Any) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.delete(transaction)
        }
    }

    fun searchTransactions(query: String): LiveData<List<Any>> {
        return repository.searchTransactions(query)
            .asLiveData()
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
}
