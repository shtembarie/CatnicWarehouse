package com.example.catnicwarehouse.scan.presentation.adapter

import android.content.Context
import android.graphics.Color
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
import com.example.catnicwarehouse.utils.colorSubstringFromCharacter
import com.example.shared.networking.network.article.ArticlesForDeliveryResponseDTO

class ArticlesSelectionAdapter(
    private val context: Context,
    private val onArticleClick: (ArticlesForDeliveryResponseDTO) -> Unit
) : ListAdapter<ArticlesForDeliveryResponseDTO, ArticlesSelectionAdapter.ArticleViewHolder>(
    ArticleDiffCallback
) {

    // 1) DiffUtil for ArticlesForDeliveryResponseDTO
    object ArticleDiffCallback : DiffUtil.ItemCallback<ArticlesForDeliveryResponseDTO>() {
        override fun areItemsTheSame(
            oldItem: ArticlesForDeliveryResponseDTO,
            newItem: ArticlesForDeliveryResponseDTO
        ): Boolean {
            // Compare unique IDs (or another unique property)
            return oldItem.articleId == newItem.articleId
        }

        override fun areContentsTheSame(
            oldItem: ArticlesForDeliveryResponseDTO,
            newItem: ArticlesForDeliveryResponseDTO
        ): Boolean {
            // Compare entire object or relevant fields
            return oldItem == newItem
        }
    }

    // 2) ViewHolder
    inner class ArticleViewHolder(
        itemView: View,
        context: Context
    ) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.title)
        private val subtitle1: TextView = itemView.findViewById(R.id.subtitle1)
        private val subtitle2: TextView = itemView.findViewById(R.id.subtitle2)
        private val unit: TextView = itemView.findViewById(R.id.unit)
        private val baseIcon: ImageView = itemView.findViewById(R.id.baseIcon)
        private val overlayIcon: ImageView = itemView.findViewById(R.id.overlayIcon)


        fun bind(article: ArticlesForDeliveryResponseDTO) {
            titleTextView.text = article.matchCode
            subtitle1.text = article.articleId
            subtitle2.visibility = View.GONE
            unit.visibility = View.GONE

            baseIcon.setImageDrawable(
                AppCompatResources.getDrawable(
                    context,
                    R.drawable.movement_default_article_icon
                )
            )
            overlayIcon.visibility = View.GONE
            itemView.setOnClickListener {
                onArticleClick(article)
            }
        }
    }


    // 3) onCreateViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.article_list_item, parent, false)
        return ArticleViewHolder(view, context)
    }

    // 4) onBindViewHolder
    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = getItem(position)
        holder.bind(article)
    }
}
