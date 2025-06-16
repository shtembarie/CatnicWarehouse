package com.example.catnicwarehouse.packing.finalisePackingList.presentation.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.databinding.ArticleListItemBinding
import com.example.catnicwarehouse.packing.shared.presentation.viewModel.PackingSharedViewModel
import com.example.catnicwarehouse.utils.colorSubstringFromCharacter
import com.example.shared.repository.movements.WarehouseStockyardInventoryEntriesResponseModel

class ArticleSelectionAdapter(
    private val interaction: ArticleSelectionAdapterInteraction,
    private val context: Context,
    private val packingSharedViewModel: PackingSharedViewModel
) :
    ListAdapter<WarehouseStockyardInventoryEntriesResponseModel, ArticleSelectionAdapter.ViewHolder>(
        DiffCallback()
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            ArticleListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            interaction,
            context,
            packingSharedViewModel
        )


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        private val binding: ArticleListItemBinding,
        private val interaction: ArticleSelectionAdapterInteraction,
        private val context: Context,
        private val packingSharedViewModel: PackingSharedViewModel
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: WarehouseStockyardInventoryEntriesResponseModel) {
            binding.apply {
                title.text = item.articleMatchCode
                subtitle1.text = item.articleId
                subtitle2.visibility = View.GONE


                val articleAmount = "${item.amount}/${item.unitCode}"
                val articleAmountSpannableString =
                    articleAmount.colorSubstringFromCharacter('/', Color.LTGRAY)
                unit.text = articleAmountSpannableString

                //In case of unique articles
                baseIcon.setImageDrawable(
                    AppCompatResources.getDrawable(
                        context,
                        R.drawable.movement_default_article_icon
                    )
                )
                overlayIcon.visibility = View.GONE
                supplierContainer.isEnabled = true


                supplierContainer.setOnClickListener {
                    interaction.onViewClicked(item)
                }
            }
        }
    }

    private class DiffCallback :
        DiffUtil.ItemCallback<WarehouseStockyardInventoryEntriesResponseModel>() {
        override fun areItemsTheSame(
            oldItem: WarehouseStockyardInventoryEntriesResponseModel,
            newItem: WarehouseStockyardInventoryEntriesResponseModel
        ): Boolean =
            oldItem.articleId == newItem.articleId

        override fun areContentsTheSame(
            oldItem: WarehouseStockyardInventoryEntriesResponseModel,
            newItem: WarehouseStockyardInventoryEntriesResponseModel
        ): Boolean =
            oldItem == newItem
    }
}

interface ArticleSelectionAdapterInteraction {
    fun onViewClicked(data: WarehouseStockyardInventoryEntriesResponseModel)
}