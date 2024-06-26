package com.example.pocketmoney

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pocketmoney.adapter.TransactionAdapter
import com.example.pocketmoney.application.TransactionApplication
import com.example.pocketmoney.viewmodel.TransactionViewModel
import com.example.pocketmoney.viewmodel.TransactionViewModelFactory

class TransactionPageActivity : AppCompatActivity() {
    private val transactionViewModel: TransactionViewModel by viewModels {
        TransactionViewModelFactory((application as TransactionApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_transaction_page)

        val recyclerView = findViewById<RecyclerView>(R.id.rvTransactionHistory)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val adapter = TransactionAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        transactionViewModel.allStudent.observe(this) { transactions ->
            transactions?.let { adapter.submitList(it) }
        }
    }
}
