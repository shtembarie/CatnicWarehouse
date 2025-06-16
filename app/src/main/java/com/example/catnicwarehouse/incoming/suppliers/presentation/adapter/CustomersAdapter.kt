package com.example.catnicwarehouse.incoming.suppliers.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.catnicwarehouse.databinding.ItemSupplierBinding
import com.example.shared.networking.network.customer.model.SearchedCustomerDTOItem
import com.example.shared.networking.network.supplier.model.SearchedVendorDTO

class CustomersAdapter(private val interaction: CustomersAdapterListInteraction) :
    ListAdapter<SearchedCustomerDTOItem, CustomersAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            ItemSupplierBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            interaction
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        private val binding: ItemSupplierBinding,
        private val interaction: CustomersAdapterListInteraction
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: SearchedCustomerDTOItem) {
            binding.apply {
                supplierName.text = item.company1
                supplierContainer.setOnClickListener {
                    interaction.onViewClicked(item)
                }
            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<SearchedCustomerDTOItem>() {
        override fun areItemsTheSame(oldItem: SearchedCustomerDTOItem, newItem: SearchedCustomerDTOItem): Boolean =
            oldItem.customerId == newItem.customerId

        override fun areContentsTheSame(
            oldItem: SearchedCustomerDTOItem,
            newItem: SearchedCustomerDTOItem
        ): Boolean =
            oldItem == newItem
    }
}

interface CustomersAdapterListInteraction {
    fun onViewClicked(data: SearchedCustomerDTOItem)
}
