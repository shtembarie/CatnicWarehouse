package com.example.catnicwarehouse.packing.addPackingItems.presentation.adapter

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
import com.example.catnicwarehouse.databinding.AddItemsForPackingItemBinding
import com.example.catnicwarehouse.databinding.ArticleListItemBinding
import com.example.catnicwarehouse.utils.colorSubstringFromCharacter
import com.example.shared.networking.network.packing.model.packingList.GetItemsForPackingResponseModelItem
import com.example.shared.networking.network.packing.model.packingList.WarehouseStockYardPicking

class PackingItemsAdapter(
    private val interaction: PackingItemsAdapterInteraction,
    private val context: Context
) : RecyclerView.Adapter<PackingItemsAdapter.ViewHolder>() {

    private val items = mutableListOf<WarehouseStockYardPicking>()

    fun submitList(newItems: List<WarehouseStockYardPicking>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            AddItemsForPackingItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            interaction,
            context
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    class ViewHolder(
        private val binding: AddItemsForPackingItemBinding,
        private val interaction: PackingItemsAdapterInteraction,
        private val context: Context
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: WarehouseStockYardPicking) {
            binding.apply {
                overlayIcon.visibility = View.GONE
                baseIcon.setImageDrawable(
                    AppCompatResources.getDrawable(context, R.drawable.hash_img)
                )
                title.text = item.amount?.toInt().toString()
                subtitle1.text = item.warehouseStockYardName
                subtitle2.visibility = View.GONE
                unit.visibility = View.GONE

                supplierContainer.isEnabled = true

                supplierContainer.setOnClickListener {
                    interaction.onViewClicked(item)
                }
            }
        }
    }
}

interface PackingItemsAdapterInteraction {
    fun onViewClicked(data: WarehouseStockYardPicking)
}
