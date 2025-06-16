package com.example.catnicwarehouse.packing.stockyards.presentation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.databinding.SearchResultItemBinding
import com.example.shared.repository.movements.WarehouseStockyardInventoryEntriesResponseModel

class StockyardsAdapter(private val interaction: WarehouseStockyardsAdapterInteraction, val context: Context) :
    ListAdapter<WarehouseStockyardInventoryEntriesResponseModel, StockyardsAdapter.ViewHolder>(
        DiffCallback()
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            SearchResultItemBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            interaction,
            context
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        private val binding: SearchResultItemBinding,
        private val interaction: WarehouseStockyardsAdapterInteraction,
        private val context:Context
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: WarehouseStockyardInventoryEntriesResponseModel) {
            binding.apply {
                supplierName.text = item.stockYardName
                supplierAddress.text = context.getString(R.string.location)
                supplierAddress.visibility = View.GONE
                supplierContainer.setOnClickListener {
                    interaction.onViewClicked(item)
                }
            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<WarehouseStockyardInventoryEntriesResponseModel>() {
        override fun areItemsTheSame(oldItem: WarehouseStockyardInventoryEntriesResponseModel, newItem: WarehouseStockyardInventoryEntriesResponseModel): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: WarehouseStockyardInventoryEntriesResponseModel, newItem: WarehouseStockyardInventoryEntriesResponseModel): Boolean =
            oldItem == newItem
    }
}

interface WarehouseStockyardsAdapterInteraction {
    fun onViewClicked(data: WarehouseStockyardInventoryEntriesResponseModel)
}