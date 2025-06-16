package com.example.catnicwarehouse.packing.shippingContainer.presentation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.databinding.ArticleListItemBinding
import com.example.shared.networking.network.packing.model.shippingContainer.getShippingContainers.Data

class ShippingContainersListAdapter(
    private val interaction: ShippingContainersListAdapterInteraction,
    private val context: Context,
) : ListAdapter<Data, ShippingContainersListAdapter.ViewHolder>(DiffCallback()) {

    private var selectedPosition: Int? = null // Keep track of the selected position

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
        private val interaction: ShippingContainersListAdapterInteraction,
        private val context: Context,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Data, isSelected: Boolean) {
            binding.apply {
                // Set title and subtitle
                title.text = item.Id
                subtitle1.visibility = View.VISIBLE
                subtitle1.text = item.Type_Description
                subtitle2.visibility = View.GONE
//                unit.text = item.

                // Change background color based on selection
                supplierContainer.setBackgroundColor(
                    if (isSelected) context.getColor(R.color.selected_row_color)
                    else context.getColor(R.color.transparent)
                )

                // Set icon based on type description
                val drawable = when (item.Type_Description) {
                    "Europalette" -> AppCompatResources.getDrawable(
                        context,
                        R.drawable.euro_pallette_icon
                    )

                    "Doppelpalette" -> AppCompatResources.getDrawable(
                        context,
                        R.drawable.doppel_pallette_icon
                    )

                    "Einwegpalette" -> AppCompatResources.getDrawable(
                        context,
                        R.drawable.einwegpalette
                    )

                    "Paket" -> AppCompatResources.getDrawable(context, R.drawable.packing_icon)
                    else -> AppCompatResources.getDrawable(context, R.drawable.packing_icon)
                }
                baseIcon.setImageDrawable(drawable)
                overlayIcon.visibility = View.GONE
                // Show tick icon for the selected row; arrow for others

//                icArrow.setImageDrawable(
//                    if (isSelected) AppCompatResources.getDrawable(context, R.drawable.orange_tick)
//                    else AppCompatResources.getDrawable(context, R.drawable.ic_to_right_arrow)
//                )
//                if (isSelected)
                    icArrow.visibility = View.VISIBLE
//                else
//                    icArrow.visibility = View.GONE

                // Handle click
                supplierContainer.setOnClickListener {
                    // Update selected position and refresh the list
                    val previousPosition = selectedPosition
                    selectedPosition = bindingAdapterPosition
                    notifyItemChanged(previousPosition ?: -1) // Refresh previously selected item
                    notifyItemChanged(selectedPosition ?: -1) // Refresh newly selected item

                    interaction.onViewClicked(item)
                }
            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<Data>() {
        override fun areItemsTheSame(oldItem: Data, newItem: Data): Boolean =
            oldItem.Id == newItem.Id

        override fun areContentsTheSame(oldItem: Data, newItem: Data): Boolean =
            oldItem == newItem
    }
}

interface ShippingContainersListAdapterInteraction {
    fun onViewClicked(data: Data)
}