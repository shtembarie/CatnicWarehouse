package com.example.catnicwarehouse.defectiveItems.stockyards.presentation.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.catnicwarehouse.databinding.CorrectingStockStockyardBinding
import com.example.catnicwarehouse.movement.articles.presentation.sealedClasses.ArticlesViewState
import com.example.shared.repository.defectiveArticles.GetDefectiveArticleUIModel
import com.example.shared.repository.movements.WarehouseStockyardInventoryEntriesResponseModel

/**
 * Created by Enoklit on 09.12.2024.
 */
class StockyardSelectionAdapter (
    private val interaction: StockyardSelectedInteraction,
    private val context: Context,
) : ListAdapter<WarehouseStockyardInventoryEntriesResponseModel, StockyardSelectionAdapter.ViewHolder>(
    DiffCallback()
){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            CorrectingStockStockyardBinding.inflate(LayoutInflater.from(parent.context),parent,false),
            interaction,
            context,
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        private val binding: CorrectingStockStockyardBinding,
        private val interaction: StockyardSelectedInteraction,
        private val context: Context,
    ): RecyclerView.ViewHolder(binding.root){

        @SuppressLint("SetTextI18n")
        fun bind(stockyardItem: WarehouseStockyardInventoryEntriesResponseModel){
            binding.apply {
                stockyardId.text = stockyardItem.stockYardName.toString()
                stockyardStatus.text = stockyardItem.articleMatchCode
                stockyardStatus.visibility = View.VISIBLE


                root.setOnClickListener {
                    interaction.onStockyardClicked(stockyardItem)
                }
            }
        }
    }
    private class DiffCallback : DiffUtil.ItemCallback<WarehouseStockyardInventoryEntriesResponseModel>() {
        override fun areItemsTheSame(
            oldItem: WarehouseStockyardInventoryEntriesResponseModel,
            newItem: WarehouseStockyardInventoryEntriesResponseModel
        ): Boolean =
            oldItem.id == newItem.id

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(
            oldItem: WarehouseStockyardInventoryEntriesResponseModel,
            newItem: WarehouseStockyardInventoryEntriesResponseModel
        ): Boolean =
            oldItem == newItem
    }
}
interface StockyardSelectedInteraction {
    fun onStockyardClicked(stockyard: WarehouseStockyardInventoryEntriesResponseModel)
}