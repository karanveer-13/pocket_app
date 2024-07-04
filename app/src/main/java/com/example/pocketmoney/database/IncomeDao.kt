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
interface IncomeDao {
    // CRUD operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(income: Income)

    @Update
    suspend fun update(income: Income)

    @Delete
    suspend fun delete(income: Income)

    @Query("SELECT * FROM income WHERE category_id = :categoryId ORDER BY date ASC")
    fun getIncomesByCategory(categoryId: Int): Flow<List<Income>>

    @Query("SELECT * FROM income WHERE source = :searchString")
    fun getIncomeByName(searchString: String): Flow<List<Income>>

    @Query("SELECT * FROM income ORDER BY date ASC")
    fun getAllIncomes(): Flow<List<Income>>

    @Query("SELECT SUM(amount) FROM income")
    fun getTotalIncome(): Flow<Double?>

    @Query("SELECT * FROM income WHERE source LIKE :query")
    fun searchIncome(query: String): LiveData<List<Income>>
}
