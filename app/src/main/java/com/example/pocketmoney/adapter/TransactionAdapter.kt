package com.example.pocketmoney.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.pocketmoney.R
import com.example.pocketmoney.database.Transaction
import java.text.SimpleDateFormat
import java.util.Locale

class TransactionAdapter(private val onDeleteClickListener: (Transaction) -> Unit) : ListAdapter<Transaction, TransactionAdapter.TransactionViewHolder>(TransactionDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.transaction_view, parent, false)
        return TransactionViewHolder(itemView,onDeleteClickListener)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = getItem(position)
        holder.bind(transaction)
    }

    class TransactionViewHolder(itemView: View, private val onDeleteClickListener: (Transaction) -> Unit) : RecyclerView.ViewHolder(itemView) {
        //private val tvID: TextView = itemView.findViewById(R.id.tvID)
        private val tvName: TextView = itemView.findViewById(R.id.tvTransactionName)
        private val tvPrice: TextView = itemView.findViewById(R.id.tvTransactionPrice)
        private val btnDelete: Button = itemView.findViewById(R.id.btnDelete)
        private val tvDate: TextView = itemView.findViewById(R.id.tvTransactionDate)

        fun bind(transaction: Transaction) {
            val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
            //tvID.text = transaction.transactionId.toString()
            tvName.text = transaction.transactionName
            tvPrice.text = transaction.transactionPrice.toString()
            btnDelete.setOnClickListener {
                onDeleteClickListener(transaction)
            }
            tvDate.text = dateFormat.format(transaction.date)
        }
    }

    private class TransactionDiffCallback : DiffUtil.ItemCallback<Transaction>() {
        override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem.transactionId == newItem.transactionId
        }

        override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem == newItem
        }
    }
}
