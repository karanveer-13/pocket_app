package com.example.pocketmoney.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(expenseItem: Expense)

    @Update
    suspend   fun update(expense: Expense)

    @Delete
    suspend fun delete(expense: Expense)

    @Query("SELECT * FROM `expense` WHERE category_id = :categoryId ORDER BY date ASC")
    fun getExpensesByCategory(categoryId: Int): Flow<List<Expense>>

    @Query("SELECT * from `expense` WHERE source = :searchString")
    fun getExpenseByName(searchString: String): Flow<List<Expense>>

    @Query("SELECT * from `expense` ORDER BY date ASC")
    fun getAllExpenses(): Flow<List<Expense>>

    @Query("SELECT SUM(amount) FROM `expense`")
    fun getTotalExpenseAmount(): Flow<Double?>

    @Query("SELECT * FROM `expense` WHERE source LIKE :query")
    fun searchExpense(query: String): LiveData<List<Expense>>

}