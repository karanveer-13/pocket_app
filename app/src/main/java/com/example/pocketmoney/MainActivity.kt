package com.example.pocketmoney

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.pocketmoney.database.*
import com.example.pocketmoney.databinding.ActivityMainBinding
import com.google.android.material.switchmaterial.SwitchMaterial
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.util.*

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var expenseDao: ExpenseDao
    lateinit var incomeDao: IncomeDao
    lateinit var categoryDao: CategoryDao

    private val sharedPrefs by lazy {
        getSharedPreferences("com.example.pocketmoney", MODE_PRIVATE)
    }

    private lateinit var categorySpinner: Spinner
    private lateinit var categoryAdapter: ArrayAdapter<String>
    private var categoryList = mutableListOf<String>()
    private var categoryMap = mutableMapOf<String, Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // Initialize the database and DAOs
        val database = PocketMoneyDatabase.getDatabase(this)
        expenseDao = database.expenseDao()
        incomeDao = database.incomeDao()
        categoryDao = database.categoryDao()

        // Initialize Spinner and its adapter
        categorySpinner = findViewById(R.id.spinnerCategory)
        categoryAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categoryList)
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = categoryAdapter

        // Retrieve categories from database
        loadCategories()

        // Set listener for Spinner selection
        categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedCategory = parent.getItemAtPosition(position).toString()
                if (selectedCategory == "Add Category") {
                    showAddCategoryDialog()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }

        // Update progress bar and balance when app opens
        updateProgressBar()
        updateBalance()

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
            val selectedCategory = categorySpinner.selectedItem.toString()
            val categoryId = categoryMap[selectedCategory] ?: 0
            if (p.isNotEmpty()) {
                try {
                    val price = p.toDouble()
                    insertInDb(tname, price, switchTransaction.isChecked, categoryId)
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

    private fun loadCategories() {
        lifecycleScope.launch {
            categoryDao.getAllCategories().collect { categories ->
                categoryList.clear()
                categoryMap.clear()
                categories.forEach {
                    categoryList.add(it.name)
                    categoryMap[it.name] = it.id
                }
                categoryList.add("Add Category") // Add option to add a new category
                categoryAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun showAddCategoryDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Add Category")

        val input = EditText(this)
        input.hint = "Category Name"
        builder.setView(input)

        builder.setPositiveButton("Add") { dialog, _ ->
            val categoryName = input.text.toString().trim()
            if (categoryName.isNotEmpty()) {
                addCategory(categoryName)
                dialog.dismiss()
            } else {
                Toast.makeText(this, "Category name cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }

    private fun addCategory(name: String) {
        lifecycleScope.launch {
            val newCategory = Category(name = name)
            categoryDao.insert(newCategory)
            loadCategories() // Refresh categories
        }
    }

    private fun insertInDb(tname: String, amount: Double, isIncome: Boolean, categoryId: Int) {
        val currentDateTime = Date()
        lifecycleScope.launch {
            try {
                if (isIncome) {
                    val income = Income(0, tname, amount, currentDateTime, categoryId)
                    incomeDao.insert(income)
                    Toast.makeText(this@MainActivity, "Income added: $amount", Toast.LENGTH_SHORT).show()
                } else {
                    val expense = Expense(0, tname, amount, currentDateTime, categoryId)
                    expenseDao.insert(expense)
                    Toast.makeText(this@MainActivity, "Expense added: $amount", Toast.LENGTH_SHORT).show()
                }
                updateProgressBar()
                updateBalance()
            } catch (e: Exception) {
                Log.e("InsertError", "Error inserting transaction: ${e.message}")
                Toast.makeText(this@MainActivity, "Failed to add transaction", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun calculateCurrentBalance(onBalanceCalculated: (Double) -> Unit) {
        lifecycleScope.launch {
            combine(
                expenseDao.getTotalExpenseAmount(),
                incomeDao.getTotalIncome()
            ) { totalExpense, totalIncome ->
                val totalExpenses = totalExpense ?: 0.0
                val totalIncomes = totalIncome ?: 0.0
                val currentBalance = totalIncomes - totalExpenses
                currentBalance
            }.collect { balance ->
                onBalanceCalculated(balance)
            }
        }
    }

    private fun updateProgressBar() {
        lifecycleScope.launch {
            combine(
                expenseDao.getTotalExpenseAmount(),
                incomeDao.getTotalIncome()
            ) { totalExpense, totalIncome ->
                val totalExpenses = totalExpense ?: 0.0
                val totalIncomes = totalIncome ?: 0.0
                if (totalIncomes > 0) {
                    ((totalExpenses / totalIncomes) * 100).toInt()
                } else {
                    0
                }
            }.collect { progress ->
                binding.progressBar.progress = progress
            }
        }
    }

    private fun updateBalance() {
        calculateCurrentBalance { balance ->
            binding.textViewBalance.text = balance.toString() // Update the balance text
        }
    }

    fun showStats(view: View) {
        val intent = Intent(this, StatsActivity::class.java)
        startActivity(intent)
    }
}
