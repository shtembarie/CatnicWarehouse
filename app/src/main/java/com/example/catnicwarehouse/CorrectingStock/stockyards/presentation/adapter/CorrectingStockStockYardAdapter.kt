package com.example.catnicwarehouse.CorrectingStock.stockyards.presentation.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.catnicwarehouse.databinding.CorrectingStockStockyardBinding
import com.example.catnicwarehouse.sharedCorrectingStock.presentation.CorrStockSharedViewModel
import com.example.shared.repository.correctingStock.model.GetCorrectionByIdUIModelCurrentInventory



/**
 * Created by Enoklit on 11.11.2024.
 */
class CorrectingStockStockYardAdapter(
    private val interaction: CorrectingStockStockYardAdapterInteraction,
    private val context: Context,
    private val corrStockSharedViewModel: CorrStockSharedViewModel
) : ListAdapter<GetCorrectionByIdUIModelCurrentInventory, CorrectingStockStockYardAdapter.ViewHolder>(
    DiffCallback()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            CorrectingStockStockyardBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            interaction,
            context,
            corrStockSharedViewModel
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        private val binding: CorrectingStockStockyardBinding,
        private val interaction: CorrectingStockStockYardAdapterInteraction,
        private val context: Context,
        private val corrStockSharedViewModel: CorrStockSharedViewModel

    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(stockyard: GetCorrectionByIdUIModelCurrentInventory) {
            binding.apply {
                stockyardId.text = stockyard.id.toString()
                stockyardId.visibility = View.GONE
                corrStockSharedViewModel.saveStockyards(listOf(stockyard))
                stockyardName.text = stockyard.name
                stockyardName.visibility = View.VISIBLE
                icCorrStock.visibility = View.VISIBLE

                // Handle item click interaction
                root.setOnClickListener {
                    interaction.onStockyardClicked(stockyard)
                }
            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<GetCorrectionByIdUIModelCurrentInventory>() {
        override fun areItemsTheSame(
            oldItem: GetCorrectionByIdUIModelCurrentInventory,
            newItem: GetCorrectionByIdUIModelCurrentInventory
        ): Boolean =
            oldItem.id == newItem.id

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(
            oldItem: GetCorrectionByIdUIModelCurrentInventory,
            newItem: GetCorrectionByIdUIModelCurrentInventory
        ): Boolean =
            oldItem == newItem
    }
}

interface CorrectingStockStockYardAdapterInteraction {
    fun onStockyardClicked(stockyard: GetCorrectionByIdUIModelCurrentInventory)
}