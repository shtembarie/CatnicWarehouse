package com.example.catnicwarehouse.packing.shippingContainer.presentation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.databinding.ArticleListItemBinding
import com.example.shared.networking.network.packing.model.shippingContainer.shippingContainerTypes.ShippingContainerTypeResponseModelItem

class ShippingContainerTypeAdapter(
    private val context: Context,
    private val containerTypes: List<ShippingContainerTypeResponseModelItem>,
    private val onItemClick: (ShippingContainerTypeResponseModelItem) -> Unit
) : RecyclerView.Adapter<ShippingContainerTypeAdapter.ViewHolder>() {

    private var selectedPosition = -1

    inner class ViewHolder(val binding: ArticleListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ShippingContainerTypeResponseModelItem, position: Int) {
            binding.apply {
                title.text = item.description
                subtitle1.visibility = View.GONE
                subtitle2.visibility = View.GONE

                // Set icon based on type description
                val drawable = when (item.description) {
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

                // Show tick icon for the selected row; arrow for others
                icArrow.setImageDrawable(
                    if (selectedPosition == position) AppCompatResources.getDrawable(
                        context,
                        R.drawable.orange_tick
                    )
                    else AppCompatResources.getDrawable(context, R.drawable.ic_to_right_arrow)
                )
                if (selectedPosition == position)
                    icArrow.visibility = View.VISIBLE
                else
                    icArrow.visibility = View.GONE

                overlayIcon.visibility = View.GONE

                // Handle item click
                root.setOnClickListener {
                    selectedPosition = position
                    notifyDataSetChanged()
                    onItemClick(item)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ArticleListItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(containerTypes[position], position)
    }

    override fun getItemCount(): Int = containerTypes.size
}
