package com.example.pocketmoney.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: Category)

    @Query("SELECT * FROM category WHERE id = :id")
    suspend fun getCategoryById(id: Int): Category?

    @Query("SELECT * FROM category /*ORDER BY name ASC*/")
    fun getAllCategories(): Flow<List<Category>>

    @Query("DELETE FROM category WHERE id = :id")
    suspend fun deleteCategory(id: Int)
}
