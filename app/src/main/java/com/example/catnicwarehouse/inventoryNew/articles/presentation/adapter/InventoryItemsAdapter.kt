package com.example.catnicwarehouse.inventoryNew.articles.presentation.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.databinding.ItemInventoryBinding
import com.example.catnicwarehouse.sharedinventory.presentation.InventorySharedViewModel
import com.example.shared.repository.inventory.model.InventoryItem

class InventoryItemsAdapter(
    private val itemClickListener: (InventoryItem) -> Unit,
) : ListAdapter<InventoryItem, InventoryItemsAdapter.ViewHolder>(InventoryDiffCallback()) {

    private var totalActualAmount: Int = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemInventoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }


    inner class ViewHolder(private val binding: ItemInventoryBinding) : RecyclerView.ViewHolder(binding.root) {
        private var currentActualAmount: Int = 0

        @SuppressLint("SetTextI18n")
        fun bind(item: InventoryItem) = with(binding) {


            inventoryId.text = item.articleId
            inventoryId.visibility = View.VISIBLE

            supplierDate.text = item.changedTimestamp
            infoActualStock.text = item.actualStock.toString()
            infoActualUnit.text = item.actualUnitCode
            targetActualUnit.text = item.targetUnitCode
            targetStock.text = item.targetStock.toString()

            val combinedText = "${item.actualStock}/${item.actualUnitCode}"
            val spannableString = SpannableString(combinedText).apply {
                setSpan(
                    ForegroundColorSpan(Color.BLACK),
                    0,
                    item.actualStock.toString().length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }

            unitCode.text = spannableString
            unitCode.setTextColor(Color.parseColor("#BEC2C3"))
            unitCode.visibility = if (combinedText == "0/null") View.GONE else View.VISIBLE

//            val updatedActualStock = inventorySharedViewModel.updatedItemAmount[item.id].takeIf { it != -1 } ?: item.actualStock
//            infoAmount.text = updatedActualStock.toString()
//            currentActualAmount = updatedActualStock

//            if (updatedActualStock != item.actualStock) {
//                infoAmount.visibility = View.VISIBLE
//                infoAmount.setTextColor(ContextCompat.getColor(itemView.context, R.color.redinv))
//                root.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.pinkonrv))
//            } else {
//                infoAmount.visibility = View.GONE
//                root.setBackgroundColor(ContextCompat.getColor(itemView.context, com.example.data.R.color.white))
//            }

            root.setOnClickListener {
//                inventorySharedViewModel.apply {
//                    saveClickedItemId(item.id)
//                    saveClickedItemIdActualStock(item.actualStock)
//                    savearticleId(item.articleId)
//                    saveArticleDescription(item.articleDescription)
//                    saveClickedArticleComment(item.comment ?: "")
//                    saveArticleMatchCode(item.articleMatchcode)
//                }
                itemClickListener(item)
            }
        }

        fun reset() {
            totalActualAmount -= currentActualAmount
//            inventorySharedViewModel.saveTotalUpdatedItemAmount(totalActualAmount)
        }
    }

    class InventoryDiffCallback : DiffUtil.ItemCallback<InventoryItem>() {
        override fun areItemsTheSame(oldItem: InventoryItem, newItem: InventoryItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: InventoryItem, newItem: InventoryItem): Boolean {
            return oldItem == newItem
        }
    }
}