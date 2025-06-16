package com.example.catnicwarehouse.Inventory.stockyardsItems.presentation.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.catnicwarehouse.Inventory.stockyardMatchFound.presentation.fragment.MatchFoundInventoryItemFragment
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.sharedinventory.presentation.InventorySharedViewModel
import com.example.shared.repository.inventory.model.InventoryItem

class InventoryItemsAdapter(
    private val inventoryItems: MutableList<InventoryItem>,
    private val itemClickListener: (Int) -> Unit,
    private val inventorySharedViewModel: InventorySharedViewModel

) : RecyclerView.Adapter<InventoryItemsAdapter.ViewHolder>() {

    private var totalActualAmount: Int = 0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_inventory, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = inventoryItems[position]
        holder.bind(item)
    }

    fun updateList(newItems: MutableList<InventoryItem>) {
        inventoryItems.clear()
        inventoryItems.addAll(newItems)
        notifyDataSetChanged()
    }


    override fun getItemCount(): Int {
        return inventoryItems.size
    }

    fun updateTotalAmounts(itemsList: List<InventoryItem>) {
        var totalActualStock = 0
        itemsList.forEach { item ->
            totalActualStock += inventorySharedViewModel.updatedItemAmount[item.id].takeIf { it != -1 }
                ?: item.actualStock
        }
        inventorySharedViewModel.saveTotalUpdatedItemAmount(totalActualStock)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val stockYardId: TextView = itemView.findViewById(R.id.inventory_id)
        private val stockYardName: TextView = itemView.findViewById(R.id.inventory_id)
        private val inventoryDate: TextView = itemView.findViewById(R.id.supplier_date)
        private val actualStock: TextView = itemView.findViewById(R.id.info_actual_stock)
        private val actualUnitCode: TextView = itemView.findViewById(R.id.info_actual_unit)
        private val targetUnitCode: TextView = itemView.findViewById(R.id.target_actual_unit)
        private val infoActualAmount: TextView = itemView.findViewById(R.id.info_amount)
        private val targetActualStock: TextView = itemView.findViewById(R.id.target_stock)
        private val unitCode: TextView = itemView.findViewById(R.id.unit_code)
        private var currentActualAmount: Int = 0


        @SuppressLint("ResourceAsColor", "SetTextI18n")
        fun bind(item: InventoryItem) {
            val formatedId = "${item.id}"
            stockYardId.text = formatedId
            stockYardId.visibility = View.GONE
            val formatedName = item.articleId
            stockYardName.text = formatedName
            stockYardName.visibility = View.VISIBLE
            inventoryDate.text = item.changedTimestamp
            actualStock.text = item.actualStock.toString()
            actualUnitCode.text = item.actualUnitCode
            targetUnitCode.text = item.targetUnitCode
            targetActualStock.text = item.targetStock.toString()
            var combinedText = "${item.actualStock}/${item.actualUnitCode}"
            unitCode.text = combinedText
            val spannableString = SpannableString(combinedText)
            spannableString.setSpan(
                ForegroundColorSpan(Color.BLACK),
                0,
                item.actualStock.toString().length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            unitCode.setTextColor(Color.parseColor("#BEC2C3"))
            unitCode.text = spannableString
            if (combinedText == "0/null") unitCode.visibility = View.GONE else unitCode.visibility = View.VISIBLE

            inventorySharedViewModel.saveTargetUnitCode(targetUnitCode.toString())
            val actualUnitCode = item.actualUnitCode
            inventorySharedViewModel.saveActualUnitCode(actualUnitCode)

            val updatedActualStock =
                inventorySharedViewModel.updatedItemAmount[item.id].takeIf { it != -1 }
                    ?: item.actualStock
            infoActualAmount.text = updatedActualStock.toString()
            currentActualAmount = updatedActualStock

            if (updatedActualStock != item.actualStock) {
                infoActualAmount.visibility = View.VISIBLE
                infoActualAmount.setTextColor(
                    ContextCompat.getColor(
                        itemView.context,
                        R.color.redinv
                    )
                )
                infoActualAmount.text = updatedActualStock.toString()
                itemView.setBackgroundColor(
                    ContextCompat.getColor(
                        itemView.context,
                        R.color.pinkonrv
                    )
                )
            } else {
                infoActualAmount.visibility = View.GONE
                itemView.setBackgroundColor(
                    ContextCompat.getColor(
                        itemView.context,
                        com.example.data.R.color.white
                    )
                )
            }

            itemView.setOnClickListener {
                inventorySharedViewModel.saveClickedItemId(item.id)
                inventorySharedViewModel.saveClickedItemIdActualStock(item.actualStock)
                inventorySharedViewModel.savearticleId(item.articleId)
                inventorySharedViewModel.saveArticleDescription(item.articleDescription)
                val comment = item.comment ?: ""
                inventorySharedViewModel.saveClickedArticleComment(comment)
                inventorySharedViewModel.saveArticleMatchCode(item.articleMatchcode)
                itemClickListener(item.id)
            }
            updateTotalAmounts(inventoryItems)

        }

        fun reset() {
            totalActualAmount -= currentActualAmount
            inventorySharedViewModel.saveTotalUpdatedItemAmount(totalActualAmount)
        }

    }
}