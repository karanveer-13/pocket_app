package com.example.pocketmoney.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

//TODO remove this DAO....Renamed the transaction to expenses so this not required anymore


@Dao
interface TransactionDao {
    //crud

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transactionItem: Transaction)

    @Update
    suspend   fun update(transaction: Transaction)

    @Delete
    suspend fun delete(transaction: Transaction)

    //Made changes to the getTransactionByName (gettransaction) function to get based on the name instead of the id

    @Query("SELECT * from `transaction` WHERE name = :searchString")
    fun getTransactionByName(searchString: String): Flow<List<Transaction>>

    @Query("SELECT * from `transaction` ORDER BY date ASC")
    fun getAllTransactions(): Flow<List<Transaction>>

    @Query("SELECT SUM(price) FROM `transaction`")
    fun getTotalTransactionPrice(): Flow<Double?>

}