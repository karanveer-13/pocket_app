package com.example.pocketmoney.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "expense",
    foreignKeys = [ForeignKey(
        entity = Category::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("category_id"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class Expense (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "source")
    val source: String,
    @ColumnInfo(name = "amount")
    val amount: Double,
    @ColumnInfo(name = "date")
    val date: Date,
    @ColumnInfo(name = "category_id")
    val categoryId: Int
)
