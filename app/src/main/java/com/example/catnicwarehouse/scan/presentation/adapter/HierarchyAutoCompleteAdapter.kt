package com.example.catnicwarehouse.scan.presentation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.scan.presentation.helper.OnItemSelectListener
import com.example.shared.networking.network.Stockyards.WarehouseStockyardsDTO

class HierarchyAutoCompleteAdapter(
    context: Context,
    private val items: List<WarehouseStockyardsDTO>,
    private val itemSelectListener: OnItemSelectListener
) : ArrayAdapter<WarehouseStockyardsDTO>(context, 0, items) {

    private val expandedItems = mutableSetOf<Int>() // Track expanded items by their IDs
    private val originalItems = items.toList() // Keep a copy of the original list for filtering
    private var matchedItemName: String? = null // Track the name of the matched item

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val item = getItem(position)
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_generic, parent, false)

        val nameTextView = view.findViewById<TextView>(R.id.name)
        val expandIcon = view.findViewById<ImageView>(R.id.expand_icon)
        val container = view.findViewById<LinearLayout>(R.id.container)

        // Display name in the format "name (id)"
        nameTextView.text = "${item?.name} (${item?.id})"

        // Set padding based on hierarchy level to create indentation
        val padding = (16 * (item?.hierarchyLevel ?: 0)).toInt()
        nameTextView.setPadding(padding, nameTextView.paddingTop, nameTextView.paddingRight, nameTextView.paddingBottom)

        // Apply different color if this item matches the search string
        if (item?.name?.equals(matchedItemName, ignoreCase = true) == true) {
            container.setBackgroundColor(ContextCompat.getColor(context, R.color.light_grey))
        } else {
            container.setBackgroundColor(ContextCompat.getColor(context, com.example.data.R.color.white))
        }

        // Set expand/collapse icon state
        expandIcon.rotation = if (expandedItems.contains(item?.id)) 270f else -270f
        expandIcon.visibility = if (item?.children?.isNotEmpty() == true) View.VISIBLE else View.GONE

        // Set click listener for expanding/collapsing
        expandIcon.setOnClickListener {
            if (expandedItems.contains(item?.id)) {
                expandedItems.remove(item?.id)
            } else {
                expandedItems.add(item?.id ?: 0)
            }
            notifyDataSetChanged() // Refresh the view
        }

        // Set click listener for selecting the item, using the interface callback
        nameTextView.setOnClickListener {
            item?.let { selectedItem ->
                itemSelectListener.onItemSelected(selectedItem)
            }
        }

        return view
    }

    override fun getCount(): Int {
        return getVisibleItems().size
    }

    override fun getItem(position: Int): WarehouseStockyardsDTO? {
        return getVisibleItems()[position]
    }

    // Override getFilter to allow searching by name only, ignoring the "(id)" part
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val results = FilterResults()
                matchedItemName = null // Reset matched item name

                if (constraint.isNullOrEmpty()) {
                    // No filter, show all items
                    results.values = originalItems
                    results.count = originalItems.size
                } else {
                    // Filter by name across the entire hierarchy
                    val filteredList = mutableListOf<WarehouseStockyardsDTO>()
                    originalItems.forEach { item ->
                        if (searchAndExpand(item, constraint.toString(), filteredList)) {
                            expandedItems.add(item.id) // Expand only the path up to the matched item
                            matchedItemName = constraint.toString() // Set the matched item name to the search string
                        }
                    }
                    results.values = filteredList.distinctBy { it.id } // Ensure no duplicates by ID
                    results.count = filteredList.size
                }
                return results
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                clear()
                addAll(results?.values as List<WarehouseStockyardsDTO>)
                notifyDataSetChanged()
            }
        }
    }

    private fun searchAndExpand(
        item: WarehouseStockyardsDTO,
        query: String,
        result: MutableList<WarehouseStockyardsDTO>
    ): Boolean {
        var isMatch = false

        // Check if the current item matches the query
        if (item.name.contains(query, ignoreCase = true)) {
            addPathToResult(item, result) // Add the full path to the matched item
            matchedItemName = item.name // Track the matched item name based on the query
            isMatch = true
            // Do not add this item to expandedItems yet so its children remain collapsed
        }

        // Recursively search children
        item.children.forEach { child ->
            val childMatch = searchAndExpand(child, query, result)
            if (childMatch) {
                expandedItems.add(item.id) // Expand only up to the matched item's parents
                isMatch = true // Mark the parent as expanded if any child matches
            }
        }

        return isMatch
    }



    // Adds the full path to the matched item to the result list
    private fun addPathToResult(item: WarehouseStockyardsDTO, result: MutableList<WarehouseStockyardsDTO>) {
        val path = mutableListOf<WarehouseStockyardsDTO>()
        var current: WarehouseStockyardsDTO? = item

        // Traverse up the hierarchy to get the full path to the root
        while (current != null && !result.contains(current)) {
            path.add(current)
            current = originalItems.find { it.id == current?.parentStockId }
        }

        // Add the path to the result in the correct order (from root to matched item)
        path.asReversed().forEach { node ->
            if (!result.contains(node)) { // Prevent duplicates in the result
                result.add(node)
            }
        }
    }

    private fun getVisibleItems(): List<WarehouseStockyardsDTO> {
        val visibleItems = mutableListOf<WarehouseStockyardsDTO>()
        items.forEach { item ->
            visibleItems.add(item)
            if (expandedItems.contains(item.id)) {
                visibleItems.addAll(getVisibleChildren(item))
            }
        }
        return visibleItems.distinctBy { it.id } // Prevent duplicate entries in visible list
    }

    private fun getVisibleChildren(item: WarehouseStockyardsDTO): List<WarehouseStockyardsDTO> {
        val children = mutableListOf<WarehouseStockyardsDTO>()
        if (expandedItems.contains(item.id)) {
            item.children.forEach { child ->
                children.add(child)
                children.addAll(getVisibleChildren(child)) // Recursive call for expanded children
            }
        }
        return children
    }
}
