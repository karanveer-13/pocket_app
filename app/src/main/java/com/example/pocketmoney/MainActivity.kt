package com.example.pocketmoney

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import androidx.lifecycle.ViewModel
import com.example.pocketmoney.database.Transaction
import com.example.pocketmoney.database.TransactionDao
import com.example.pocketmoney.database.TransactionRoomDatabase
import com.example.pocketmoney.databinding.ActivityMainBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var dao : TransactionDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        var  database = TransactionRoomDatabase.getDatabase(this)
        dao = database.itemDao()
        binding.submitButton.setOnClickListener{
            var tname = binding.editTextText.text.toString()
            var p = findViewById<EditText>(R.id.editTextNumberDecimal).text.toString()
            var price = p.toDouble()
            insertInDb(tname,price)
        }
    }

    private fun insertInDb(tname: String, price: Double) {

        var currentDateTime: java.util.Date = java.util.Date()
        GlobalScope.launch {
            var item = Transaction(0,tname,price)
            dao.insert(item)
            dao.getTotalTransactionPrice()
                .map { total ->
                    total?.let { ((it / 3000) * 100).toInt() } ?: 0
                }
                .collect { progress ->
                    binding.progressBar.progress = progress
                }
        }
    }
}