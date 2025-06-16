package com.example.catnicwarehouse.defectiveItems.stockyards.presentation.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.catnicwarehouse.databinding.CorrectingStockStockyardBinding
import com.example.shared.repository.defectiveArticles.GetDefectiveArticleUIModel
import androidx.recyclerview.widget.ListAdapter


/**
 * Created by Enoklit on 04.12.2024.
 */
class DefectiveArticlesAdapter(
    private val interaction: DefectiveItemsAdapterInteraction,
    private val context: Context,
) : ListAdapter<GetDefectiveArticleUIModel, DefectiveArticlesAdapter.ViewHolder>(
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
        private val interaction: DefectiveItemsAdapterInteraction,
        private val context: Context,
    ): RecyclerView.ViewHolder(binding.root){

        @SuppressLint("SetTextI18n")
        fun bind(stockyardItem: GetDefectiveArticleUIModel){
            binding.apply {
                stockyardId.text = stockyardItem.articleId
                articleInfoActuell.text = stockyardItem.defectiveAmount.toString()
                stockyardStatus.visibility = View.GONE


                root.setOnClickListener {
                    interaction.onStockyardClicked(stockyardItem)
                }
            }
        }
    }
    private class DiffCallback : DiffUtil.ItemCallback<GetDefectiveArticleUIModel>() {
        override fun areItemsTheSame(
            oldItem: GetDefectiveArticleUIModel,
            newItem: GetDefectiveArticleUIModel
        ): Boolean =
            oldItem.id == newItem.id

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(
            oldItem: GetDefectiveArticleUIModel,
            newItem: GetDefectiveArticleUIModel
        ): Boolean =
            oldItem == newItem
    }
}
interface DefectiveItemsAdapterInteraction {
    fun onStockyardClicked(defectiveArticles: GetDefectiveArticleUIModel)
}