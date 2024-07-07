package com.example.pocketmoney.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pocketmoney.R
import com.example.pocketmoney.database.Category
import com.example.pocketmoney.database.Expense
import com.example.pocketmoney.database.Income
import com.example.pocketmoney.database.TransactionRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class CategoryAdapter(
    private val categories: List<Category>,
    private val totalAmount: Double,
    private val categoryMap: Map<Int, Category>,
    private val isExpense: Boolean,
    private val repository: TransactionRepository // Inject TransactionRepository here
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val categoryNameTextView: TextView = itemView.findViewById(R.id.categoryTextView)
        private val categoryProgressBar: ProgressBar = itemView.findViewById(R.id.categoryProgressBar)
        private val amountTextView: TextView = itemView.findViewById(R.id.amountTextView)

        fun bind(category: Category) {
            categoryNameTextView.text = category.name

            // Calculate category amount based on whether it's expense or income
            val coroutineScope = CoroutineScope(Dispatchers.IO)
            coroutineScope.launch {
                val categoryAmount = if (isExpense) {
                    // Retrieve expenses for this category and calculate the total amount
                    val expensesForCategory = repository.getExpensesByCategory(category.id).firstOrNull()
                    expensesForCategory?.let { expenses ->
                        expenses.sumOf { expense -> expense.amount }
                    } ?: 0.0
                } else {
                    // Retrieve incomes for this category and calculate the total amount
                    val incomesForCategory = repository.getIncomesByCategory(category.id).firstOrNull()
                    incomesForCategory?.let { incomes ->
                        incomes.sumOf { income -> income.amount }
                    } ?: 0.0
                }

                launch(Dispatchers.Main) {
                    val percentage = if (totalAmount > 0) (categoryAmount / totalAmount * 100).toInt() else 0
                    categoryProgressBar.progress = percentage
                    amountTextView.text = String.format("%.2f", categoryAmount)
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.categorystats, parent, false)
        return CategoryViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
        holder.bind(category)
    }

    override fun getItemCount(): Int {
        return categories.size
    }
}
