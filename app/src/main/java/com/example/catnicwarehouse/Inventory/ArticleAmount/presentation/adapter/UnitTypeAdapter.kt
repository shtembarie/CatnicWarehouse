package com.example.catnicwarehouse.Inventory.ArticleAmount.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.catnicwarehouse.R
/**
 * Created by Enoklit on 18.07.2024.
 */

class UnitTypeAdapter(
    private val unitTypes: List<String>,
    private val onUnitTypeClick: (String) -> Unit,
    private var selectedUnitCode: String?,
) : RecyclerView.Adapter<UnitTypeAdapter.UnitTypeViewHolder>() {

    private var selectedPosition = unitTypes.indexOf(selectedUnitCode)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UnitTypeViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_unit_type, parent, false)
        return UnitTypeViewHolder(view)
    }

    override fun onBindViewHolder(holder: UnitTypeViewHolder, position: Int) {
        val unitType = unitTypes[position]
        holder.bind(unitType, position, selectedPosition)
    }

    override fun getItemCount(): Int = unitTypes.size

    inner class UnitTypeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val checkIcon: ImageView = itemView.findViewById(R.id.checkIcon)
        private val unitTypeText: TextView = itemView.findViewById(R.id.unitTypeText)
        init {
            itemView.setOnClickListener {
                val previousPosition = selectedPosition
                selectedPosition = bindingAdapterPosition
                notifyItemChanged(previousPosition)
                notifyItemChanged(selectedPosition)
                onUnitTypeClick(unitTypes[bindingAdapterPosition])
            }
        }
        fun bind(unitType: String, position: Int, selectedPosition: Int) {
            unitTypeText.text = unitType
            checkIcon.visibility = if (position == selectedPosition) View.VISIBLE else View.GONE
            itemView.isEnabled = true
            unitTypeText.setTextColor(
                ContextCompat.getColor(
                    itemView.context,
                    android.R.color.black
                )
            )
        }
    }

    fun updateSelectedUnitType(newUnitCode: String) {
        val newPosition = unitTypes.indexOf(newUnitCode)
        if (newPosition != selectedPosition) {
            val oldPosition = selectedPosition
            selectedPosition = newPosition
            selectedUnitCode = newUnitCode
            notifyItemChanged(oldPosition)
            notifyItemChanged(newPosition)
        }
    }
}


