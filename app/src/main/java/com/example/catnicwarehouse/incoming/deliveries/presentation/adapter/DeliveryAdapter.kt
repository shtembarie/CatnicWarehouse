package com.example.catnicwarehouse.incoming.deliveries.presentation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.databinding.ItemDeliveryBinding
import com.example.catnicwarehouse.incoming.deliveries.domain.model.DeliveryUIModel
import com.example.catnicwarehouse.tools.reParseDate

class DeliveryAdapter(private val interaction: DeliveryAdapterListInteraction) : ListAdapter<DeliveryUIModel, DeliveryAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            ItemDeliveryBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            interaction,
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        private val binding: ItemDeliveryBinding,
        private val interaction: DeliveryAdapterListInteraction,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: DeliveryUIModel) {
            binding.apply {
                delivryId.text = item.title
                supplierTxt.text = item.supplier
                supplierDate.text = reParseDate(item.date)
                if(item.state=="END"){
                    imageState.setImageResource((R.color.light_green))
                }else {
                    imageState.setImageResource((R.color.dark_orange))
                }

                deliveryItemContainer.setOnClickListener {
                    interaction.onViewClicked(item)
                }
            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<DeliveryUIModel>() {
        override fun areItemsTheSame(oldItem: DeliveryUIModel, newItem: DeliveryUIModel): Boolean =
            oldItem.title == newItem.title

        override fun areContentsTheSame(oldItem: DeliveryUIModel, newItem: DeliveryUIModel): Boolean =
            oldItem == newItem
    }
}

interface DeliveryAdapterListInteraction {
    fun onViewClicked(data: DeliveryUIModel)
}
