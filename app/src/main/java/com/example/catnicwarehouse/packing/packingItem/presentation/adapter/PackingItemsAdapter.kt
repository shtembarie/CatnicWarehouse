package com.example.catnicwarehouse.packing.packingItem.presentation.adapter

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
import com.example.catnicwarehouse.utils.colorSubstringFromCharacter
import com.example.shared.networking.network.packing.model.packingItem.PackingItem

class PackingItemsAdapter(
    private val interaction: PackingItemsAdapterInteraction,
    private val context: Context,
    private val showArrow: Boolean
) :
    ListAdapter<PackingItem, PackingItemsAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            ArticleListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            interaction,
            context,
            showArrow
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        private val binding: ArticleListItemBinding,
        private val interaction: PackingItemsAdapterInteraction,
        private val context: Context,
        private val showArrow: Boolean
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: PackingItem) {
            binding.apply {
                title.text = item.articleId
                subtitle1.visibility = View.GONE
                subtitle2.visibility = View.GONE

                if(showArrow){
                    icArrow.visibility = View.VISIBLE
                }else{
                    icArrow.visibility = View.GONE
                }


                val articleAmount = "${item.packedAmount}/${item.amount} ${item.unitCode}"
                val articleAmountSpannableString =
                    articleAmount.colorSubstringFromCharacter(' ', Color.LTGRAY)
                unit.text = articleAmountSpannableString

                baseIcon.setImageDrawable(
                    AppCompatResources.getDrawable(
                        context,
                        R.drawable.packing_list_article_icon
                    )
                )
                overlayIcon.visibility = View.GONE

                supplierContainer.setOnClickListener {
                    interaction.onViewClicked(item)
                }

            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<PackingItem>() {
        override fun areItemsTheSame(
            oldItem: PackingItem,
            newItem: PackingItem
        ): Boolean =
            oldItem.articleId == newItem.articleId

        override fun areContentsTheSame(
            oldItem: PackingItem,
            newItem: PackingItem
        ): Boolean =
            oldItem == newItem
    }
}

interface PackingItemsAdapterInteraction {
    fun onViewClicked(data: PackingItem)
}