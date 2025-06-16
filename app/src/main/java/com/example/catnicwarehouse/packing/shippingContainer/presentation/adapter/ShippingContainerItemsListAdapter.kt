package com.example.catnicwarehouse.packing.shippingContainer.presentation.adapter

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
import com.example.shared.networking.network.packing.model.shippingContainer.ShippingContainerPackingListItemsByShippingContainer.Item

class ShippingContainerItemsListAdapter(
    private val interaction: ShippingContainersItemsListAdapterInteraction,
    private val context: Context,
) : ListAdapter<Item, ShippingContainerItemsListAdapter.ViewHolder>(DiffCallback()) {

    private var selectedPosition: Int? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            ArticleListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            interaction,
            context
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(
            getItem(position),
            position == selectedPosition
        ) // Pass if this position is selected
    }

    inner class ViewHolder(
        private val binding: ArticleListItemBinding,
        private val interaction: ShippingContainersItemsListAdapterInteraction,
        private val context: Context,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Item, isSelected: Boolean) {
            binding.apply {
                // Set title and subtitle
                title.text = item.articleId
                subtitle1.visibility = View.GONE
                subtitle2.visibility = View.GONE
                unit.text = "${item.packedAmount}/${item.amount} ${item.unitCode}"

                // Set icon based on type description
                val drawable = AppCompatResources.getDrawable(
                    context,
                    R.drawable.article_icon_img
                )
                baseIcon.setImageDrawable(drawable)
                overlayIcon.visibility = View.GONE
                icArrow.visibility = View.GONE

                // Handle click
                supplierContainer.setOnClickListener {
//                    // Update selected position and refresh the list
//                    val previousPosition = selectedPosition
//                    selectedPosition = bindingAdapterPosition
//                    notifyItemChanged(previousPosition ?: -1) // Refresh previously selected item
//                    notifyItemChanged(selectedPosition ?: -1) // Refresh newly selected item
//
//                    interaction.onViewClicked(item)
                }
            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<Item>() {
        override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean =
            oldItem.orderItemLgid == newItem.orderItemLgid

        override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean =
            oldItem == newItem
    }
}

interface ShippingContainersItemsListAdapterInteraction {
    fun onViewClicked(data: Item)
}