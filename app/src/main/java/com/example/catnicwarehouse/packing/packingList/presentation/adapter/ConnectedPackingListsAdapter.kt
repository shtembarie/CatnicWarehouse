package com.example.catnicwarehouse.packing.packingList.presentation.adapter

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
import com.example.shared.networking.network.packing.model.packingList.ConnectedPackingList

class ConnectedPackingListsAdapter(
    private val interaction: ConnectedPackingListsInteraction,
    private val context: Context
) : ListAdapter<ConnectedPackingList, ConnectedPackingListsAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            ArticleListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            interaction,
            context
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        private val binding: ArticleListItemBinding,
        private val interaction: ConnectedPackingListsInteraction,
        private val context: Context
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ConnectedPackingList) {
            binding.apply {
                title.text = item.id
                subtitle1.text = "Items: ${item.items.size}"
                subtitle1.visibility = View.VISIBLE
                subtitle2.visibility = View.GONE

                icArrow.visibility = View.VISIBLE

                // Display default icon for connected packing lists
                baseIcon.setImageDrawable(
                    AppCompatResources.getDrawable(
                        context,
                        R.drawable.packing_icon
                    )
                )
                overlayIcon.visibility = View.GONE

                supplierContainer.setOnClickListener {
                    interaction.onConnectedPackingListClicked(item)
                }
            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<ConnectedPackingList>() {
        override fun areItemsTheSame(oldItem: ConnectedPackingList, newItem: ConnectedPackingList): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: ConnectedPackingList, newItem: ConnectedPackingList): Boolean =
            oldItem == newItem
    }
}

interface ConnectedPackingListsInteraction {
    fun onConnectedPackingListClicked(data: ConnectedPackingList)
}
