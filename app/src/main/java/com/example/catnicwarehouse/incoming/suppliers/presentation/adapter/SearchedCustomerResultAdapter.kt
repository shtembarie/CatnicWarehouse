package com.example.catnicwarehouse.incoming.suppliers.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.catnicwarehouse.databinding.SearchResultItemBinding
import com.example.shared.networking.network.customer.model.SearchedCustomerDTOItem

class SearchedCustomerResultAdapter(
    private var items: List<SearchedCustomerDTOItem>,
    private val onClick: (SearchedCustomerDTOItem) -> Unit
) : RecyclerView.Adapter<SearchedCustomerResultAdapter.ViewHolder>() {

    // DiffUtil Callback
    class SearchDiffCallback(
        private val oldList: List<SearchedCustomerDTOItem>,
        private val newList: List<SearchedCustomerDTOItem>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].customerId == newList[newItemPosition].customerId
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }

    // ViewHolder class with ViewBinding
    class ViewHolder(val binding: SearchResultItemBinding) : RecyclerView.ViewHolder(binding.root)

    // Create new views with ViewBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            SearchResultItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    // Replace the contents of a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.binding.supplierName.text = item.company1
        holder.binding.supplierAddress.text = item.bupA_id
        holder.itemView.setOnClickListener {
            onClick(item)
        }
    }

    // Return the size of the dataset
    override fun getItemCount(): Int = items.size

    // Update the list using DiffUtil to calculate the differences
    fun updateList(newItems: List<SearchedCustomerDTOItem>) {
        val diffResult = DiffUtil.calculateDiff(SearchDiffCallback(this.items, newItems))
        this.items = newItems
        diffResult.dispatchUpdatesTo(this)
    }
}
