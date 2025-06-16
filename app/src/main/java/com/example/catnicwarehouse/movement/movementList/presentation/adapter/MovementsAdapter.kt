package com.example.catnicwarehouse.movement.movementList.presentation.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.catnicwarehouse.databinding.MovementListItemBinding
import com.example.catnicwarehouse.databinding.SearchResultItemBinding
import com.example.catnicwarehouse.utils.colorSubstringFromCharacter
import com.example.shared.repository.movements.MovementItem

class MovementsAdapter(private val interaction: MovementsAdapterInteraction,private val showArrow:Boolean) :
    ListAdapter<MovementItem, MovementsAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            MovementListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            interaction,
            showArrow
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        private val binding: MovementListItemBinding,
        private val interaction: MovementsAdapterInteraction,
        private val showArrow: Boolean
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: MovementItem) {
            binding.apply {
                title.text = item.articleId
                subtitle1.text = item.sourceWarehouseStockYardName
                subtitle2.visibility = View.GONE

                val articleAmount = "${item.amount}/${item.unitCode}"
                val articleAmountSpannableString =
                    articleAmount.colorSubstringFromCharacter('/', Color.LTGRAY)
                unit.text = articleAmountSpannableString

                if(showArrow)
                    icArrow.visibility = View.VISIBLE
                else
                    icArrow.visibility = View.GONE

                supplierContainer.setOnClickListener {
                    interaction.onViewClicked(item)
                }

            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<MovementItem>() {
        override fun areItemsTheSame(oldItem: MovementItem, newItem: MovementItem): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: MovementItem, newItem: MovementItem): Boolean =
            oldItem == newItem
    }
}

interface MovementsAdapterInteraction {
    fun onViewClicked(data: MovementItem)
}