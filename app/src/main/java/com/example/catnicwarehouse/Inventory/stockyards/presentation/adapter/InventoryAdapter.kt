package com.example.catnicwarehouse.Inventory.stockyards.presentation.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.sharedinventory.presentation.InventorySharedViewModel
import com.example.shared.repository.inventory.model.CurrentInventoryWarehouseStockYardsModel

class InventoryAdapter(
    private var stockyards: List<CurrentInventoryWarehouseStockYardsModel>,
    private val itemClickListener: (Int) -> Unit,
    private val inventorySharedViewModel: InventorySharedViewModel,

    ) : RecyclerView.Adapter<InventoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.inventories, parent, false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val stockyard = stockyards[position]
        holder.bind(stockyard)
    }

    override fun getItemCount(): Int {
        return stockyards.size
    }
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val stockyardId: TextView = itemView.findViewById(R.id.inventory_id)
        private val stockyardName: TextView = itemView.findViewById(R.id.inventory_id)
        private val stockyardInventoriedStatus: CardView = itemView.findViewById(R.id.ic_inventory)

        @SuppressLint("SetTextI18n")
        fun bind(stockyard: CurrentInventoryWarehouseStockYardsModel) {
            stockyardId.text = "${stockyard.id}"
            stockyardId.visibility = View.GONE
            stockyardName.text = stockyard.name
            stockyardName.visibility = View.VISIBLE
            if (stockyard.inventoried) {
                stockyardInventoriedStatus.setCardBackgroundColor(ContextCompat.getColor(itemView.context, R.color.light_green))
            } else {
                stockyardInventoriedStatus.setCardBackgroundColor(ContextCompat.getColor(itemView.context, R.color.light_grey))
            }
            inventorySharedViewModel.saveStockyards(stockyards)
            itemView.setOnClickListener {
                itemClickListener(stockyard.id)
            }
        }
    }
}
