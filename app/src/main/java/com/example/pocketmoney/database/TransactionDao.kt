package com.example.pocketmoney.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    //crud

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transactionItem: Transaction)

    @Update
    suspend   fun update(transaction: Transaction)

    @Delete
    suspend fun delete(transaction: Transaction)

    @Query("SELECT * from `transaction` WHERE id = :id")
    fun getTransaction(id: Int): Flow<Transaction>

    @Query("SELECT * from `transaction` ORDER BY name ASC")
    fun getTransactions(): Flow<List<Transaction>>

    @Query("SELECT SUM(price) FROM `transaction`")
    fun getTotalTransactionPrice(): Flow<Double?>

}