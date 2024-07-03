package com.example.pocketmoney

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pocketmoney.MainActivity
import com.example.pocketmoney.R
import com.example.pocketmoney.application.TransactionApplication
import com.example.pocketmoney.database.Income
import com.example.pocketmoney.database.PocketMoneyDatabase
import com.example.pocketmoney.databinding.ActivityTransactionPageBinding
import com.example.pocketmoney.viewmodel.TransactionViewModel
import example.pocketmoney.adapter.TransactionAdapter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TransactionPageActivity : AppCompatActivity() {
    lateinit var binding: ActivityTransactionPageBinding
    private lateinit var adapter: TransactionAdapter
    private val transactionViewModel: TransactionViewModel by viewModels {
        TransactionViewModel.TransactionViewModelFactory((application as TransactionApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_transaction_page)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        val recyclerView = findViewById<RecyclerView>(R.id.rvTransactionHistory)
        val etSearchTransaction = findViewById<EditText>(R.id.etSearchTransaction)
        val rgFilter = findViewById<RadioGroup>(R.id.rgFilter)
        //insertDummyIncomeData()
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.black))

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Transactions"

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        adapter = TransactionAdapter { transaction ->
            transactionViewModel.delete(transaction)
            Toast.makeText(this, "Deleted transaction", Toast.LENGTH_SHORT).show()
        }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        transactionViewModel.allTransactions.observe(this) { transactions ->
            transactions?.let { adapter.submitList(it) }
        }

        rgFilter.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rbAll -> showAllTransactions()
                R.id.rbIncome -> showIncomeTransactions()
                R.id.rbExpense -> showExpenseTransactions()
            }
        }

        etSearchTransaction.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchTransactions(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
        })

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(this@TransactionPageActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        })
    }
    private fun showAllTransactions() {
        transactionViewModel.allTransactions.observe(this) { transactions ->
            transactions?.let { adapter.submitList(it) }
        }
    }

    private fun showIncomeTransactions() {
        transactionViewModel.allIncomes.observe(this) { incomes ->
            incomes?.let { adapter.submitList(it) }
        }
    }

    private fun showExpenseTransactions() {
        transactionViewModel.allExpenses.observe(this) { expenses ->
            expenses?.let { adapter.submitList(it) }
        }
    }

    // Function to insert dummy income data into the database
    /*private fun insertDummyIncomeData() {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dummyIncomes = listOf(
            Income(0, "Salary", 5000.0, dateFormat.parse("2024-07-01") ?: Date()),
            Income(0, "Freelancing", 1500.0, dateFormat.parse("2024-07-02") ?: Date()),
            Income(0, "Interest", 200.0, dateFormat.parse("2024-07-03") ?: Date())
        )

        for (income in dummyIncomes) {
            transactionViewModel.insert(income)
        }
    }*/


    private fun searchTransactions(query: String) {
        transactionViewModel.searchTransactions(query).observe(this) { transactions ->
            transactions?.let {
                adapter.submitList(it)
            }
        }
    }
}
