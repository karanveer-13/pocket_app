package com.example.pocketmoney.viewmodel

import androidx.lifecycle.*
import com.example.pocketmoney.database.Category
import com.example.pocketmoney.database.Expense
import com.example.pocketmoney.database.Income
import com.example.pocketmoney.database.TransactionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class TransactionViewModel(private val repository: TransactionRepository) : ViewModel() {

    val allTransactions: LiveData<List<Any>> = repository.getAllTransactionsCombined().asLiveData()
    val allIncomes: LiveData<List<Income>> = repository.getIncomes().asLiveData()
    val allExpenses: LiveData<List<Expense>> = repository.getExpenses().asLiveData()
    val allCategories: LiveData<Map<Int, Category>> = liveData {
        val categories = repository.getAllCategories().first()
        emit(categories.associateBy { it.id })
    }

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
        return repository.searchTransactions(query).asLiveData()
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
