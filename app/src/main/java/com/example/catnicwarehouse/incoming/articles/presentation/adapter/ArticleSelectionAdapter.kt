package com.example.catnicwarehouse.incoming.articles.presentation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.catnicwarehouse.databinding.ArticleListItemBinding
import com.example.catnicwarehouse.shared.presentation.viewModel.SharedViewModelNew
import com.example.shared.networking.network.article.ArticlesForDeliveryResponseDTO

class ArticleSelectionAdapter(
    private val interaction: ArticleSelectionAdapterInteraction,
    private val context: Context,
    private val movementsSharedViewModel: SharedViewModelNew
) :
    ListAdapter<ArticlesForDeliveryResponseDTO, ArticleSelectionAdapter.ViewHolder>(
        DiffCallback()
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            ArticleListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            interaction,
            context,
            movementsSharedViewModel
        )


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        private val binding: ArticleListItemBinding,
        private val interaction: ArticleSelectionAdapterInteraction,
        private val context: Context,
        private val sharedViewModelNew: SharedViewModelNew
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ArticlesForDeliveryResponseDTO) {
            binding.apply {
                title.text = item.matchCode
                subtitle1.text = item.articleId
                subtitle2.visibility = View.GONE

                overlayIcon.visibility = View.GONE
                supplierContainer.isEnabled = true


                supplierContainer.setOnClickListener {
                    interaction.onViewClicked(item)
                }
            }
        }
    }

    private class DiffCallback :
        DiffUtil.ItemCallback<ArticlesForDeliveryResponseDTO>() {
        override fun areItemsTheSame(
            oldItem: ArticlesForDeliveryResponseDTO,
            newItem: ArticlesForDeliveryResponseDTO
        ): Boolean =
            oldItem.articleId == newItem.articleId

        override fun areContentsTheSame(
            oldItem: ArticlesForDeliveryResponseDTO,
            newItem: ArticlesForDeliveryResponseDTO
        ): Boolean =
            oldItem == newItem
    }
}

interface ArticleSelectionAdapterInteraction {
    fun onViewClicked(data: ArticlesForDeliveryResponseDTO)
}