package com.example.catnicwarehouse.incoming.unloadingStockyards.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.catnicwarehouse.databinding.SearchResultItemBinding
import com.example.shared.networking.network.Stockyards.WarehouseStockyardsDTO

class UnloadingStockyardsAdapter(private val interaction: UnloadingStockyardsAdapterInteraction) :
    ListAdapter<WarehouseStockyardsDTO, UnloadingStockyardsAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            SearchResultItemBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            interaction
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        private val binding: SearchResultItemBinding,
        private val interaction: UnloadingStockyardsAdapterInteraction
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: WarehouseStockyardsDTO) {
            binding.apply {
                supplierName.text = item.name
                supplierAddress.text = item.warehouseCode
                supplierContainer.setOnClickListener {
                    interaction.onViewClicked(item)
                }
            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<WarehouseStockyardsDTO>() {
        override fun areItemsTheSame(oldItem: WarehouseStockyardsDTO, newItem: WarehouseStockyardsDTO): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: WarehouseStockyardsDTO, newItem: WarehouseStockyardsDTO): Boolean =
            oldItem == newItem
    }
}

interface UnloadingStockyardsAdapterInteraction {
    fun onViewClicked(data: WarehouseStockyardsDTO)
}
