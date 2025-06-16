package com.example.catnicwarehouse.packing.finalisePackingList.presentation.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.databinding.ArticleListItemBinding
import com.example.catnicwarehouse.databinding.SearchResultItemBinding
import com.example.catnicwarehouse.packing.shared.presentation.viewModel.PackingSharedViewModel
import com.example.catnicwarehouse.utils.colorSubstringFromCharacter
import com.example.shared.networking.network.packing.model.defaultPackingZone.DefaultPackingZoneResultModel
import com.example.shared.repository.movements.WarehouseStockyardInventoryEntriesResponseModel

class DefaultPackingZonesAdapter(
    private val interaction: DefaultPackingZoneAdapterInteraction,
    private val context: Context,
    private val showArrow:Boolean = true
) :
    ListAdapter<DefaultPackingZoneResultModel, DefaultPackingZonesAdapter.ViewHolder>(
        DiffCallback()
    ) {

    private var selectedPosition: Int? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            SearchResultItemBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            interaction,
            context,
            showArrow
        )


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val isSelected = position == selectedPosition
        holder.bind(getItem(position),isSelected,position)
    }

    inner class ViewHolder(
        private val binding: SearchResultItemBinding,
        private val interaction: DefaultPackingZoneAdapterInteraction,
        private val context: Context,
        private val showArrow: Boolean
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: DefaultPackingZoneResultModel,isSelected: Boolean,position:Int) {
            binding.apply {
                supplierName.text = item.name
                supplierAddress.text = context.getString(R.string.pending_to_picking)


                val selectedDrawable = if (isSelected) R.drawable.orange_tick else R.drawable.ic_to_right_arrow
                icArrow.setImageDrawable(context.getDrawable(selectedDrawable))

                icArrow.visibility = if (showArrow || isSelected) View.VISIBLE else View.GONE


                supplierContainer.setOnClickListener {
                    selectedPosition = position
                    notifyDataSetChanged()
                    interaction.onViewClicked(item)
                }
            }
        }
    }

    private class DiffCallback :
        DiffUtil.ItemCallback<DefaultPackingZoneResultModel>() {
        override fun areItemsTheSame(
            oldItem: DefaultPackingZoneResultModel,
            newItem: DefaultPackingZoneResultModel
        ): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: DefaultPackingZoneResultModel,
            newItem: DefaultPackingZoneResultModel
        ): Boolean =
            oldItem == newItem
    }
}

interface DefaultPackingZoneAdapterInteraction {
    fun onViewClicked(data: DefaultPackingZoneResultModel)
}