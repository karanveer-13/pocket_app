package com.example.pocketmoney.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pocketmoney.R

data class CategoryPercentage(val category: String, val amountSpent: Float, val totalAmount: Float)

class CategoryAdapter(private val categories: List<CategoryPercentage>) :
    RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    class CategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val categoryTextView: TextView = view.findViewById(R.id.categoryTextView)
        val amountTextView: TextView = view.findViewById(R.id.amountTextView)
        val progressBar: ProgressBar = view.findViewById(R.id.categoryProgressBar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.categorystats, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
        holder.categoryTextView.text = category.category
        holder.amountTextView.text = String.format("%.2f", category.amountSpent)
        val progress = ((category.amountSpent / category.totalAmount) * 100).toInt()
        holder.progressBar.progress = progress
    }

    override fun getItemCount() = categories.size
}
