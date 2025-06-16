package com.example.catnicwarehouse.inventoryNew.stockyards.presentation.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.catnicwarehouse.R
import com.example.shared.repository.inventory.model.CurrentInventoryWarehouseStockYardsModel

class InventoryStockyardsAdapter(
    private val itemClickListener: (CurrentInventoryWarehouseStockYardsModel) -> Unit
) : ListAdapter<CurrentInventoryWarehouseStockYardsModel, InventoryStockyardsAdapter.ViewHolder>(StockyardDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.inventories, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), itemClickListener)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val stockyardName: TextView = itemView.findViewById(R.id.inventory_id)
        private val stockyardInventoriedStatus: CardView = itemView.findViewById(R.id.ic_inventory)

        @SuppressLint("SetTextI18n")
        fun bind(stockyard: CurrentInventoryWarehouseStockYardsModel, clickListener: (CurrentInventoryWarehouseStockYardsModel) -> Unit) {
            stockyardName.text = stockyard.name
            stockyardName.visibility = View.VISIBLE
            stockyardInventoriedStatus.setCardBackgroundColor(
                ContextCompat.getColor(
                    itemView.context,
//                    if (stockyard.inventoried) R.color.light_green else
                        R.color.light_grey
                )
            )
            itemView.setOnClickListener {
                clickListener(stockyard)
            }
        }
    }

    class StockyardDiffCallback : DiffUtil.ItemCallback<CurrentInventoryWarehouseStockYardsModel>() {
        override fun areItemsTheSame(oldItem: CurrentInventoryWarehouseStockYardsModel, newItem: CurrentInventoryWarehouseStockYardsModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CurrentInventoryWarehouseStockYardsModel, newItem: CurrentInventoryWarehouseStockYardsModel): Boolean {
            return oldItem == newItem
        }
    }
}
