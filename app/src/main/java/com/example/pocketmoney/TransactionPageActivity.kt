package com.example.pocketmoney

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
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
import com.example.pocketmoney.adapter.TransactionAdapter
import com.example.pocketmoney.application.TransactionApplication
import com.example.pocketmoney.databinding.ActivityTransactionPageBinding
import com.example.pocketmoney.viewmodel.TransactionViewModel
import com.example.pocketmoney.viewmodel.TransactionViewModelFactory

class TransactionPageActivity : AppCompatActivity() {
    lateinit var binding: ActivityTransactionPageBinding
    private lateinit var adapter: TransactionAdapter
    private val transactionViewModel: TransactionViewModel by viewModels {
        TransactionViewModelFactory((application as TransactionApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_transaction_page)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.black))

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Transactions"

        val recyclerView = findViewById<RecyclerView>(R.id.rvTransactionHistory)
        val etSearchTransaction = findViewById<EditText>(R.id.etSearchTransaction)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        adapter = TransactionAdapter { transaction ->
            transactionViewModel.delete(transaction)
            Toast.makeText(this, "Deleted transaction: ${transaction.transactionName}", Toast.LENGTH_SHORT).show()
        }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        transactionViewModel.allStudent.observe(this) { transactions ->
            transactions?.let { adapter.submitList(it) }
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

    private fun searchTransactions(query: String) {
        transactionViewModel.searchTransactions(query).observe(this) { transactions ->
            transactions?.let {
                adapter.submitList(it)
            }
        }
    }
}
