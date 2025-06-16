package com.example.catnicwarehouse.incoming.amountItem.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.catnicwarehouse.databinding.ItemUnitMeterTypeBinding
import com.example.shared.networking.network.article.ArticleUnit


class UnitMeterTypeAdapter(
    private val unitMeterTypes: List<ArticleUnit>?,
    private var selectedUnitType: String?,
    private val itemClickListener: (ArticleUnit) -> Unit
) : RecyclerView.Adapter<UnitMeterTypeAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemUnitMeterTypeBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemUnitMeterTypeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder.binding) {
            title.text = unitMeterTypes?.get(position)?.unitCode
            checkImg.isVisible = unitMeterTypes?.get(position)?.unitCode == selectedUnitType

            viewContainer.setOnClickListener {
                unitMeterTypes?.get(position)?.let { it1 -> itemClickListener(it1) }
            }
        }
    }

    override fun getItemCount(): Int = unitMeterTypes?.size ?:0
}
