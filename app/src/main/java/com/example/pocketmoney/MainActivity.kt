package com.example.pocketmoney

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import androidx.lifecycle.lifecycleScope
import com.example.pocketmoney.database.Transaction
import com.example.pocketmoney.database.TransactionDao
import com.example.pocketmoney.database.TransactionRoomDatabase
import com.example.pocketmoney.databinding.ActivityMainBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.Date

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var dao: TransactionDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // Initialize the database and DAO
        val database = TransactionRoomDatabase.getDatabase(this)
        dao = database.transactionDao()

        // Update progress bar when app opens
        updateProgressBar()

        // Handle submit button click
        binding.submitButton.setOnClickListener {
            val tname = binding.editTextText.text.toString()
            val p = findViewById<EditText>(R.id.editTextNumberDecimal).text.toString()
            val price = p.toDouble()
            insertInDb(tname, price)
        }
        //Handle Transaction History button click
        binding.btnTransactionHistory.setOnClickListener {
            val intent = Intent(this, TransactionPageActivity::class.java)
            startActivity(intent)
        }
    }

    private fun insertInDb(tname: String, price: Double) {
        val currentDateTime = Date()
        lifecycleScope.launch {
            val item = Transaction(0, tname, price)
            dao.insert(item)
            updateProgressBar()
        }
    }

    private fun updateProgressBar() {
        lifecycleScope.launch {
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
