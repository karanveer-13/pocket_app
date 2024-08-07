package com.example.pocketmoney.database


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime
import java.util.Date

//TODO remove this entity...we are not using this anymore instead of this only the expense entity is there


@Entity  //create table transaction(id integer,name,price,date)
data class Transaction (
    @PrimaryKey(autoGenerate = true)
    val transactionId: Int= 0,
    @ColumnInfo(name = "name")
    val transactionName: String,
    @ColumnInfo(name = "price")
    val transactionPrice: Double,
    @ColumnInfo(name = "date")
    val date: Date
)