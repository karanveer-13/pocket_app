package example.pocketmoney.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.pocketmoney.R
import com.example.pocketmoney.database.Category
import com.example.pocketmoney.database.Expense
import com.example.pocketmoney.database.Income
import java.text.SimpleDateFormat
import java.util.Locale

class TransactionAdapter(private val onDeleteClickListener: (Any) -> Unit, private val categoryMap: Map<Int, Category>) :
    ListAdapter<Any, TransactionAdapter.TransactionViewHolder>(TransactionDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.transaction_view, parent, false)
        return TransactionViewHolder(itemView, onDeleteClickListener)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = getItem(position)
        holder.bind(transaction)
    }

    inner class TransactionViewHolder(
        itemView: View,
        private val onDeleteClickListener: (Any) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        private val tvName: TextView = itemView.findViewById(R.id.tvTransactionName)
        private val tvPrice: TextView = itemView.findViewById(R.id.tvTransactionPrice)
        private val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)
        private val tvDate: TextView = itemView.findViewById(R.id.tvTransactionDate)
        private val tvCategory: TextView = itemView.findViewById(R.id.tvTransactionCategory)
        fun bind(transaction: Any) {
            val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
            when (transaction) {
                is Expense -> {
                    tvName.text = transaction.source
                    tvPrice.text = transaction.amount.toString()
                    tvDate.text = dateFormat.format(transaction.date)
                    tvCategory.text = categoryMap[transaction.categoryId]?.name ?: "Unknown"
                }
                is Income -> {
                    tvName.text = transaction.source
                    tvPrice.text = transaction.amount.toString()
                    tvDate.text = dateFormat.format(transaction.date)
                    tvCategory.text = categoryMap[transaction.categoryId]?.name ?: "Unknown"
                }
                else -> throw IllegalArgumentException("Unknown transaction type")
            }
            btnDelete.setOnClickListener {
                onDeleteClickListener(transaction)
            }
        }
    }

    private class TransactionDiffCallback : DiffUtil.ItemCallback<Any>() {
        override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
            return when {
                oldItem is Expense && newItem is Expense -> oldItem.id == newItem.id
                oldItem is Income && newItem is Income -> oldItem.id == newItem.id
                else -> false
            }
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
            return oldItem == newItem
        }
    }
}
