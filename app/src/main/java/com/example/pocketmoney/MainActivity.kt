package com.example.pocketmoney

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import android.widget.ToggleButton
import androidx.lifecycle.lifecycleScope
import com.example.pocketmoney.database.Expense
import com.example.pocketmoney.database.ExpenseDao
import com.example.pocketmoney.database.Income
import com.example.pocketmoney.database.IncomeDao
import com.example.pocketmoney.database.PocketMoneyDatabase
import com.example.pocketmoney.databinding.ActivityMainBinding
import com.google.android.material.switchmaterial.SwitchMaterial
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.Date

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var expenseDao: ExpenseDao
    lateinit var incomeDao: IncomeDao

    private val sharedPrefs by lazy {
        getSharedPreferences("com.example.pocketmoney", MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // Initialize the database and DAOs
        val database = PocketMoneyDatabase.getDatabase(this)
        expenseDao = database.expenseDao()
        incomeDao = database.incomeDao()

        // Retrieve and set allowance
        val storedAllowance = getStoredAllowance()
        if (storedAllowance != null) {
            binding.editTextAllowance.setText(storedAllowance.toString())
        }

        // Set allowance button click handler
        binding.editTextAllowance.setOnClickListener {
            val allowanceText = binding.editTextAllowance.text.toString()
            if (allowanceText.isNotEmpty()) {
                try {
                    val allowance = allowanceText.toDouble()
                    setStoredAllowance(allowance)
                    updateProgressBar()
                    Toast.makeText(this, "Allowance set to $allowance", Toast.LENGTH_SHORT).show()
                } catch (e: NumberFormatException) {
                    Toast.makeText(this, "Invalid allowance value", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please enter a valid allowance", Toast.LENGTH_SHORT).show()
            }
        }

        // Update progress bar when app opens
        updateProgressBar()

        val switchTransaction = findViewById<SwitchMaterial>(R.id.switchTransaction)
        switchTransaction.text = if (switchTransaction.isChecked) "Income" else "Expense"
        switchTransaction.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Log.d("Switch", "Income")
                switchTransaction.text = "Income"
            } else {
                switchTransaction.text = "Expense"
            }
        }

        // Handle submit button click
        binding.submitButton.setOnClickListener {
            val tname = binding.editTextText.text.toString()
            val p = findViewById<EditText>(R.id.editTextNumberDecimal).text.toString()
            if (p.isNotEmpty()) {
                try {
                    val price = p.toDouble()
                    insertInDb(tname, price, switchTransaction.isChecked)
                } catch (e: NumberFormatException) {
                    Toast.makeText(this, "Invalid price value", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please enter a valid price", Toast.LENGTH_SHORT).show()
            }
        }

        // Handle Transaction History button click
        binding.btnTransactionHistory.setOnClickListener {
            val intent = Intent(this@MainActivity, TransactionPageActivity::class.java)
            startActivity(intent)
        }
    }

    private fun insertInDb(tname: String, amount: Double, isIncome: Boolean) {
        val currentDateTime = Date()
        lifecycleScope.launch {
            if (isIncome) {
                val income = Income(0, tname, amount, currentDateTime)
                incomeDao.insert(income)
                adjustAllowance(amount) // Increase allowance
                Toast.makeText(this@MainActivity, "Income added: $amount", Toast.LENGTH_SHORT).show()
            } else {
                val expense = Expense(0, tname, amount, currentDateTime)
                expenseDao.insert(expense)
                adjustAllowance(-amount) // Decrease allowance
                Toast.makeText(this@MainActivity, "Expense added: $amount", Toast.LENGTH_SHORT).show()
            }
            updateProgressBar()
        }
    }

    private fun adjustAllowance(amount: Double) {
        val currentAllowance = getStoredAllowance() ?: 0.0 // Default allowance if not set
        val newAllowance = currentAllowance + amount
        setStoredAllowance(newAllowance)
    }

    private fun updateProgressBar() {
        val allowance = getStoredAllowance() ?: 0.0 // Default allowance if not set
        lifecycleScope.launch {
            combine(
                expenseDao.getTotalExpenseAmount(),
                incomeDao.getTotalIncome()
            ) { totalExpense, totalIncome ->
                totalExpense?.let {
                    totalIncome?.let {
                        ((totalExpense / (totalIncome + allowance)) * 100).toInt()
                    } ?: ((totalExpense / allowance) * 100).toInt()
                } ?: 0
            }.collect { progress ->
                binding.progressBar.progress = progress
                binding.editTextAllowance.setText(allowance.toString()) // Update the allowance text
            }
        }
    }

    private fun setStoredAllowance(allowance: Double) {
        val currentDateTime = Date()
        val income = Income(0, "Allowance", allowance, currentDateTime)

        lifecycleScope.launch {
            with(sharedPrefs.edit()) {
                putFloat("allowance", allowance.toFloat())
                apply()
            }
            updateProgressBar()
        }
    }

    private fun getStoredAllowance(): Double? {
        return if (sharedPrefs.contains("allowance")) {
            sharedPrefs.getFloat("allowance", 0.0f).toDouble()
        } else {
            null
        }
    }
}
