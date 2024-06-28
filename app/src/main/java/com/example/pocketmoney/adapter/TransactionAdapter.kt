package com.example.pocketmoney.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.pocketmoney.R
import com.example.pocketmoney.database.Transaction
import java.text.SimpleDateFormat
import java.util.Locale

class TransactionAdapter : ListAdapter<Transaction, TransactionAdapter.TransactionViewHolder>(TransactionDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.transaction_view, parent, false)
        return TransactionViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = getItem(position)
        holder.bind(transaction)
    }

    class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //private val tvID: TextView = itemView.findViewById(R.id.tvID)
        private val tvName: TextView = itemView.findViewById(R.id.tvTransactionName)
        private val tvPrice: TextView = itemView.findViewById(R.id.tvTransactionPrice)
        private val tvDate: TextView = itemView.findViewById(R.id.tvTransactionDate)

        fun bind(transaction: Transaction) {
            val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
            //tvID.text = transaction.transactionId.toString()
            tvName.text = transaction.transactionName
            tvPrice.text = transaction.transactionPrice.toString()
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
