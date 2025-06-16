package com.example.catnicwarehouse.incoming.suppliers.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.catnicwarehouse.databinding.ItemSupplierBinding
import com.example.shared.networking.network.supplier.model.SearchedVendorDTO

class VendorsAdapter(private val interaction: SupplierAdapterListInteraction) :
    ListAdapter<SearchedVendorDTO, VendorsAdapter.ViewHolder>(DiffCallback()) {

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
        private val interaction: SupplierAdapterListInteraction
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: SearchedVendorDTO) {
            binding.apply {
                supplierName.text = item.company1
                supplierContainer.setOnClickListener {
                    interaction.onViewClicked(item)
                }
            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<SearchedVendorDTO>() {
        override fun areItemsTheSame(oldItem: SearchedVendorDTO, newItem: SearchedVendorDTO): Boolean =
            oldItem.vendorId == newItem.vendorId

        override fun areContentsTheSame(
            oldItem: SearchedVendorDTO,
            newItem: SearchedVendorDTO
        ): Boolean =
            oldItem == newItem
    }
}

interface SupplierAdapterListInteraction {
    fun onViewClicked(data: SearchedVendorDTO)
}
