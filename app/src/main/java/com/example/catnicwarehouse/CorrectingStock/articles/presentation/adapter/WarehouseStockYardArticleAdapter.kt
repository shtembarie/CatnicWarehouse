package com.example.catnicwarehouse.CorrectingStock.articles.presentation.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.engine.Resource
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.databinding.CorrectingStockStockyardBinding
import com.example.catnicwarehouse.sharedCorrectingStock.presentation.CorrStockSharedViewModel
import com.example.shared.repository.correctingStock.model.GetWarehouseStockYardsArticlesByIdUIModel
import com.example.shared.repository.inventory.model.InventoryItem

/**
 * Created by Enoklit on 13.11.2024.
 */
class WarehouseStockYardArticleAdapter(
    private val interaction: WarehouseStockYardArticleAdapterInteraction,
    private val context: Context,
    private val corrStockSharedViewModel: CorrStockSharedViewModel,
): ListAdapter<GetWarehouseStockYardsArticlesByIdUIModel, WarehouseStockYardArticleAdapter.ViewHolder>(
    DiffCallBack()
) {
    override fun onCreateViewHolder(parent:ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            CorrectingStockStockyardBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            interaction,
            context,
            corrStockSharedViewModel
        )
    override fun onBindViewHolder(holder:ViewHolder, position: Int){
        holder.bind(getItem(position))
    }

    class ViewHolder(
        private val binding: CorrectingStockStockyardBinding,
        private val interaction: WarehouseStockYardArticleAdapterInteraction,
        private val context: Context,
        private val corrStockSharedViewModel: CorrStockSharedViewModel
    ): RecyclerView.ViewHolder(binding.root){
        @SuppressLint("SetTextI18n")
        fun bind(stockyard: GetWarehouseStockYardsArticlesByIdUIModel){
            binding.apply {
                stockyardId.text = stockyard.id.toString()
                stockyardId.visibility = View.GONE
                stockyardName.text = stockyard.stockYardName
                stockyardName.visibility = View.VISIBLE
                stockyardStatus.text = context.getString(R.string.artikel_number_corr, stockyard.articleId)
                stockyardStatus.visibility = View.VISIBLE
                val combinedText = "${stockyard.amount}/${stockyard.unit}"
                articleInfo.text = combinedText
                // Creating a SpannableString to style the text
                val spannableString = SpannableString(combinedText)
                // Setting the color for stockyard.amount (black)
                spannableString.setSpan(
                    ForegroundColorSpan(Color.BLACK),
                    0, // Start index
                    stockyard.amount.toString().length, // End index
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                // Setting the color for the rest of the text
                articleInfo.setTextColor(Color.parseColor("#BEC2C3"))
                // Setting the styled text to articleInfo
                articleInfo.text = spannableString


                corrStockSharedViewModel.saveItemIdsAndArticleIds(listOf(GetWarehouseStockYardsArticlesByIdUIModel(
                    stockyard.id,
                    stockyard.stockYardId,
                    stockyard.stockYardName,
                    stockyard.articleId,
                    stockyard.articleMatchCode,
                    stockyard.articleDescription,
                    stockyard.amount,
                    stockyard.unit,
                    stockyard.defectiveArticles,
                    stockyard.isMoving
                )))

                //Handle item click interaction
                root.setOnClickListener {
                    interaction.onStockyardClicked(stockyard)
                }

            }
        }
    }

    private class DiffCallBack : DiffUtil.ItemCallback<GetWarehouseStockYardsArticlesByIdUIModel>(){
        override fun areItemsTheSame(
            oldItem: GetWarehouseStockYardsArticlesByIdUIModel,
            newItem: GetWarehouseStockYardsArticlesByIdUIModel
        ): Boolean =
            oldItem.id == newItem.id

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(
            oldItem: GetWarehouseStockYardsArticlesByIdUIModel,
            newItem: GetWarehouseStockYardsArticlesByIdUIModel
        ): Boolean =
            oldItem == newItem

    }
}

interface WarehouseStockYardArticleAdapterInteraction {
    fun onStockyardClicked(stockyard: GetWarehouseStockYardsArticlesByIdUIModel)
}