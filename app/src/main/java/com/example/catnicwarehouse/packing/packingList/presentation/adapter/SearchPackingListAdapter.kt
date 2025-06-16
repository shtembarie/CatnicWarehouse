package com.example.catnicwarehouse.packing.packingList.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.databinding.SearchResultItemBinding
import com.example.shared.networking.network.packing.model.packingList.SearchPackingListDTO

class SearchPackingListAdapter(
    private var items: List<SearchPackingListDTO>,
    private val onClick: (SearchPackingListDTO) -> Unit
) : RecyclerView.Adapter<SearchPackingListAdapter.ViewHolder>() {

    // DiffUtil Callback
    class PackingListDiffCallback(
        private val oldList: List<SearchPackingListDTO>,
        private val newList: List<SearchPackingListDTO>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].packingListId == newList[newItemPosition].packingListId
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }

    // ViewHolder class with ViewBinding
    class ViewHolder(val binding: SearchResultItemBinding) : RecyclerView.ViewHolder(binding.root)

    // Create new views with ViewBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = SearchResultItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    // Replace the contents of a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.binding.supplierName.text = item.packingListId
        holder.binding.supplierAddress.visibility = View.GONE
        holder.itemView.setOnClickListener { onClick(item) }
    }

    // Return the size of the dataset
    override fun getItemCount(): Int = items.size

    // Update the list using DiffUtil to calculate the differences
    fun updateList(newItems: List<SearchPackingListDTO>) {
        val diffResult = DiffUtil.calculateDiff(PackingListDiffCallback(this.items, newItems))
        this.items = newItems
        diffResult.dispatchUpdatesTo(this)
    }
}
