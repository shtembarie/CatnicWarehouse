package com.example.catnicwarehouse.incoming.amountItem.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.catnicwarehouse.databinding.ItemUnitMeterTypeBinding
import com.example.catnicwarehouse.incoming.matchFound.presentation.sealedClass.DefectiveReason

class DefectiveReasonAdapter(
    private val defectiveReasons: Array<DefectiveReason>,
    private var selectedDefectiveReason: DefectiveReason,
    private val itemClickListener: (DefectiveReason) -> Unit
) : RecyclerView.Adapter<DefectiveReasonAdapter.ViewHolder>() {

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
            title.text = defectiveReasons[position].value
            checkImg.isVisible = defectiveReasons[position] == selectedDefectiveReason

            viewContainer.setOnClickListener {
                itemClickListener(defectiveReasons[position])
            }
        }
    }

    override fun getItemCount(): Int = defectiveReasons.size
}
