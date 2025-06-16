package com.example.catnicwarehouse.inventoryNew.articles.presentation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.catnicwarehouse.R
import com.example.shared.repository.movements.WarehouseStockyardInventoryEntriesResponseModel

class ArticlesBasedOnStockyardEntriesSelectionAdapter(
    private val context: Context,
    private val onItemClick: (WarehouseStockyardInventoryEntriesResponseModel) -> Unit
) : ListAdapter<WarehouseStockyardInventoryEntriesResponseModel, ArticlesBasedOnStockyardEntriesSelectionAdapter.ViewHolder>(
    DIFF_CALLBACK
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.article_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.title)
        private val subtitle1: TextView = itemView.findViewById(R.id.subtitle1)
        private val subtitle2: TextView = itemView.findViewById(R.id.subtitle2)
        private val unit: TextView = itemView.findViewById(R.id.unit)
        private val baseIcon: ImageView = itemView.findViewById(R.id.baseIcon)
        private val overlayIcon: ImageView = itemView.findViewById(R.id.overlayIcon)

        fun bind(entry: WarehouseStockyardInventoryEntriesResponseModel) {
            titleTextView.text = entry.articleMatchCode
            subtitle1.text = entry.articleId
            subtitle2.visibility = View.GONE
            unit.visibility = View.GONE

            baseIcon.setImageDrawable(
                AppCompatResources.getDrawable(
                    context,
                    R.drawable.movement_default_article_icon
                )
            )
            overlayIcon.visibility = View.GONE
            itemView.setOnClickListener { onItemClick(entry) }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<WarehouseStockyardInventoryEntriesResponseModel>() {
            override fun areItemsTheSame(
                oldItem: WarehouseStockyardInventoryEntriesResponseModel,
                newItem: WarehouseStockyardInventoryEntriesResponseModel
            ) = oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: WarehouseStockyardInventoryEntriesResponseModel,
                newItem: WarehouseStockyardInventoryEntriesResponseModel
            ) = oldItem == newItem
        }
    }
}