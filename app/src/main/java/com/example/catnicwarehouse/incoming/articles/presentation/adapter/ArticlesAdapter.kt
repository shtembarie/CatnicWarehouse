package com.example.catnicwarehouse.incoming.articles.presentation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.databinding.ItemDeliveryBinding
import com.example.shared.networking.network.delivery.model.getDelivery.ArticleItem


class ArticlesAdapter(
    private val interaction: ArticlesAdapterListInteraction,
    private val context: Context
) :
    ListAdapter<ArticleItem, ArticlesAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            ItemDeliveryBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            interaction
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position),context)
    }

    class ViewHolder(
        private val binding: ItemDeliveryBinding,
        private val interaction: ArticlesAdapterListInteraction
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ArticleItem, context: Context) {
            binding.apply {
                iconBadge.setImageDrawable(
                    AppCompatResources.getDrawable(
                        context,
                        R.drawable.hash_img
                    )
                )
                imageState.visibility = View.GONE
                delivryId.text = item.articleId
                supplierTxt.visibility = View.GONE
                supplierDate.visibility = View.GONE
                articleQty.visibility = View.VISIBLE
                articleQty.text = "${item.amount} ${item.unitCode}"
                deliveryItemContainer.setOnClickListener {
                    interaction.onViewClicked(item)
                }
            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<ArticleItem>() {
        override fun areItemsTheSame(oldItem: ArticleItem, newItem: ArticleItem): Boolean =
            oldItem.articleId == newItem.articleId

        override fun areContentsTheSame(oldItem: ArticleItem, newItem: ArticleItem): Boolean =
            oldItem == newItem
    }
}

interface ArticlesAdapterListInteraction {
    fun onViewClicked(data: ArticleItem)
}
