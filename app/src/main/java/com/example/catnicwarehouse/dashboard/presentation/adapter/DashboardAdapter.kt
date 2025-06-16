package com.example.catnicwarehouse.dashboard.presentation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.databinding.DashboardItemBinding
import com.example.catnicwarehouse.dashboard.presentation.adapter.model.DashBoardModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class DashboardAdapter @Inject constructor(@ApplicationContext context: Context) :
    RecyclerView.Adapter<DashboardAdapter.ViewHolder>() {
    private var dashboardListInteraction: DashboardListInteraction? = null
    private val dashboardList = mutableListOf<DashBoardModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(DashboardItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(dashboardList[position])

    override fun getItemCount(): Int = dashboardList.size

    inner class ViewHolder(private val binding: DashboardItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DashBoardModel) {
            binding.apply {
                dashboardTitleId.text = item.title
                dashboardImage.setImageResource(item.res)

                // If not enabled, reduce alpha or set grayscale, etc.
                if (!item.enabled) {
                    binding.dashboardContainer.visibility = View.GONE
                    binding.dashboardContainer.alpha = 0.4f
                    binding.dashboardContainer.isEnabled = false
                } else {
                    binding.dashboardContainer.visibility = View.VISIBLE
                    binding.dashboardContainer.alpha = 1f
                    binding.dashboardContainer.isEnabled = true
                }

                dashboardContainer.setOnClickListener {
                    dashboardListInteraction?.onViewClicked(item)
                }
            }
        }
    }
    /**
     * Clears the old list and sets the new items.
     */
    fun setItems(items: List<DashBoardModel>) {
        dashboardList.clear()
        dashboardList.addAll(items)
        notifyDataSetChanged()
    }


    fun setInteraction(dashboardListInteraction: DashboardListInteraction) {
        this.dashboardListInteraction = dashboardListInteraction
    }
}



interface DashboardListInteraction {
    fun onViewClicked(item: DashBoardModel)
}
