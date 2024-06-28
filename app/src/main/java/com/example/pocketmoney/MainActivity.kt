package com.example.pocketmoney

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
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

    private val sharedPrefs by lazy {
        getSharedPreferences("com.example.pocketmoney", MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // Initialize the database and DAO
        val database = TransactionRoomDatabase.getDatabase(this)
        dao = database.transactionDao()

        // Retrieve and set allowance
        val storedAllowance = getStoredAllowance()
        if (storedAllowance != null) {
            binding.editTextAllowance.setText(storedAllowance.toString())
        }

        // Set allowance button click handler
        binding.editTextAllowance.setOnClickListener {
            val allowanceText = binding.editTextAllowance.text.toString()
            if (allowanceText.isNotEmpty()) {
                val allowance = allowanceText.toDouble()
                setStoredAllowance(allowance)
                updateProgressBar()
                Toast.makeText(this, "Allowance set to $allowance", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Please enter a valid allowance", Toast.LENGTH_SHORT).show()
            }
        }

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
            val item = Transaction(0, tname, price, currentDateTime)
            dao.insert(item)
            updateProgressBar()
        }
    }

    private fun updateProgressBar() {
        val allowance = getStoredAllowance() ?: 3000.0 // Default allowance if not set
        lifecycleScope.launch {
            dao.getTotalTransactionPrice()
                .map { total ->
                    total?.let { ((it / allowance) * 100).toInt() } ?: 0
                }
                .collect { progress ->
                    binding.progressBar.progress = progress
                }
        }
    }

    private fun setStoredAllowance(allowance: Double) {
        with(sharedPrefs.edit()) {
            putFloat("allowance", allowance.toFloat())
            apply()
        }
    }

    private fun getStoredAllowance(): Double? {
        return if (sharedPrefs.contains("allowance")) {
            sharedPrefs.getFloat("allowance", 3000.0f).toDouble()
        } else {
            null
        }
    }
}
