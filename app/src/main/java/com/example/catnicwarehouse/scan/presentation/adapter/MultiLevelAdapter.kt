package com.example.catnicwarehouse.scan.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.scan.presentation.helper.OnItemSelectListener
import com.example.shared.networking.network.Stockyards.WarehouseStockyardsDTO

class MultiLevelAdapter(
    private val items: List<WarehouseStockyardsDTO>, // List of all items
    private val selectedItemId: Int, // ID of the selected item to start displaying hierarchy
    private val itemSelectListener: OnItemSelectListener
) : RecyclerView.Adapter<MultiLevelAdapter.HierarchyViewHolder>() {

    private val expandedItems = mutableSetOf<Int>() // Track expanded items by their IDs
    private val visibleItems = mutableListOf<WarehouseStockyardsDTO>() // List of visible items
    private var rootItem: WarehouseStockyardsDTO? = null // The selected root item to display hierarchy from

    init {
        // Find the root item based on selectedItemId and populate visible items if found
        rootItem = findItemById(items, selectedItemId)
        rootItem?.let {
            populateVisibleItems()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HierarchyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_generic, parent, false)
        return HierarchyViewHolder(view)
    }

    override fun onBindViewHolder(holder: HierarchyViewHolder, position: Int) {
        val item = visibleItems[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return visibleItems.size
    }

    // ViewHolder class to bind item data
    inner class HierarchyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.name)
        private val expandIcon: ImageView = itemView.findViewById(R.id.expand_icon)

        fun bind(item: WarehouseStockyardsDTO) {
            // Display name in the format "name (id)"
            nameTextView.text = "${item.name} (${item.id})"

            // Set padding based on hierarchy level to create indentation
            val padding = (16 * item.hierarchyLevel).toInt()
            itemView.setPadding(padding, itemView.paddingTop, itemView.paddingRight, itemView.paddingBottom)

            // Set the initial rotation state of the arrow
            expandIcon.rotation = if (expandedItems.contains(item.id)) 270f else -270f // 90f for down, 0f for right-facing

            // Show or hide expand icon based on the presence of children
            expandIcon.visibility = if (!item.children.isNullOrEmpty()) View.VISIBLE else View.GONE

            // Handle expand/collapse functionality
            expandIcon.setOnClickListener {
                if (expandedItems.contains(item.id)) {
                    // Collapse item and recursively collapse all its children
                    collapseItemAndChildren(item)
                    expandIcon.rotation = 0f // Set arrow to right-facing
                } else {
                    // Expand item
                    expandedItems.add(item.id)
                    expandIcon.rotation = 90f // Set arrow to down-facing
                }
                populateVisibleItems() // Update visible items based on the expanded state
                notifyDataSetChanged() // Refresh the RecyclerView
            }

            // Set click listener for selecting the item, using the interface callback
            nameTextView.setOnClickListener {
                item.let { selectedItem ->
                    itemSelectListener.onItemSelected(selectedItem)
                }
            }
        }
    }


    // Function to collapse an item and all its children recursively
    private fun collapseItemAndChildren(item: WarehouseStockyardsDTO) {
        expandedItems.remove(item.id) // Remove the item itself from expanded items
        item.children.forEach { child ->
            collapseItemAndChildren(child) // Recursively collapse each child
        }
    }

    // Populate visible items based on the expanded state of each item
    private fun populateVisibleItems() {
        visibleItems.clear()
        rootItem?.let { addVisibleItems(it, visibleItems) }
    }

    // Recursively add items to the visible list based on expansion state
    private fun addVisibleItems(item: WarehouseStockyardsDTO, visibleList: MutableList<WarehouseStockyardsDTO>) {
        visibleList.add(item) // Add the item itself

        // If the item is expanded, add its children
        if (expandedItems.contains(item.id)) {
            item.children.forEach { child ->
                addVisibleItems(child, visibleList) // Recursive call to add children
            }
        }
    }

    // Helper function to find an item by ID within a list of items
    private fun findItemById(items: List<WarehouseStockyardsDTO>?, id: Int): WarehouseStockyardsDTO? {
        items?.forEach { item ->
            if (item.id == id) return item
            val foundInChild = findItemById(item.children, id)
            if (foundInChild != null) return foundInChild
        }
        return null
    }
}
