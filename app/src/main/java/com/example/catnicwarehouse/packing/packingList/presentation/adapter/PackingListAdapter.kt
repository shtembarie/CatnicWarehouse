package com.example.catnicwarehouse.packing.packingList.presentation.adapter

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.databinding.ArticleListItemBinding
import com.example.catnicwarehouse.packing.shared.PackingItemStatus
import com.example.catnicwarehouse.packing.shared.presentation.viewModel.PackingSharedViewModel
import com.example.shared.networking.network.packing.model.packingList.AssignedPackingListItem


class PackingListAdapter(
    private val interaction: PackingListAdapterInteraction,
    private val context: Context,
    private val viewModel:PackingSharedViewModel
) :
    ListAdapter<AssignedPackingListItem, PackingListAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            ArticleListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            interaction,
            context,
            viewModel
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        private val binding: ArticleListItemBinding,
        private val interaction: PackingListAdapterInteraction,
        private val context: Context,
        private val viewModel:PackingSharedViewModel
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: AssignedPackingListItem) {
            binding.apply {
                val titleValue = if (item.packingListGroupCode.isNullOrEmpty() || viewModel.selectedAssignedPackingListGroupItem?.isInsideAGroup==true) item.id else item.packingListGroupName

                val companyCityText =
                    "${item.deliveryAddressCompany1} / ${item.deliveryAddressCity}"
                val fullText = "$titleValue  $companyCityText"

                val spannable = SpannableString(fullText)
                val startIndex = fullText.indexOf(companyCityText)
                val endIndex = startIndex + companyCityText.length
                spannable.setSpan(
                    ForegroundColorSpan(context.getColor(R.color.label_medium)),
                    startIndex,
                    endIndex,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                spannable.setSpan(
                    AbsoluteSizeSpan(15, true),
                    startIndex,
                    endIndex,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                title.text =  if (item.packingListGroupCode.isNullOrEmpty() || viewModel.selectedAssignedPackingListGroupItem?.isInsideAGroup==true) spannable else titleValue
                unit.visibility = View.GONE
                subtitle1.visibility = View.GONE
                subtitle2.visibility = View.GONE
                icArrow.visibility = View.VISIBLE
                //In case of not paused packing items - default
                if (item.appStatus != PackingItemStatus.PAU.name) {
                    if (item.packingListGroupCode.isNullOrEmpty() || viewModel.selectedAssignedPackingListGroupItem?.isInsideAGroup==true) {
                        baseIcon.setImageDrawable(
                            AppCompatResources.getDrawable(
                                context,
                                R.drawable.packing_list_article_icon
                            )
                        )
                    } else {
                        baseIcon.setImageDrawable(
                            AppCompatResources.getDrawable(
                                context,
                                R.drawable.packing_list_group_icon
                            )
                        )
                    }
                    overlayIcon.visibility = View.GONE
                } else {
                    if (item.packingListGroupCode.isNullOrEmpty()) {
                        baseIcon.setImageDrawable(
                            AppCompatResources.getDrawable(
                                context,
                                R.drawable.packing_pause_icon
                            )
                        )
                        overlayIcon.setImageDrawable(
                            AppCompatResources.getDrawable(
                                context,
                                R.drawable.pause
                            )
                        )
                    }
                    overlayIcon.visibility = View.VISIBLE
                }


                supplierContainer.setOnClickListener {
                    interaction.onViewClicked(item)
                }

            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<AssignedPackingListItem>() {
        override fun areItemsTheSame(
            oldItem: AssignedPackingListItem,
            newItem: AssignedPackingListItem
        ): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: AssignedPackingListItem,
            newItem: AssignedPackingListItem
        ): Boolean =
            oldItem == newItem
    }
}

interface PackingListAdapterInteraction {
    fun onViewClicked(data: AssignedPackingListItem)
}