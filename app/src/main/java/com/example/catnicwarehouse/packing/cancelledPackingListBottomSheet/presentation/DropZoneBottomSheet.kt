package com.example.catnicwarehouse.packing.cancelledPackingListBottomSheet.presentation

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.databinding.DefaultPackingZoneBottomSheetBinding
import com.example.catnicwarehouse.packing.finalisePackingList.presentation.adapter.DefaultPackingZoneAdapterInteraction
import com.example.catnicwarehouse.packing.finalisePackingList.presentation.adapter.DefaultPackingZonesAdapter
import com.example.shared.networking.network.packing.model.defaultPackingZone.DefaultPackingZoneResultModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DropZoneBottomSheet(
    private val defaultPackingZones: List<DefaultPackingZoneResultModel>,
    private val onDropOffClick: (DefaultPackingZoneResultModel?) -> Unit
) : BottomSheetDialogFragment(), DefaultPackingZoneAdapterInteraction {

    private var _binding: DefaultPackingZoneBottomSheetBinding? = null
    private val binding get() = _binding!!

    private var selectedZone: DefaultPackingZoneResultModel? = null
    private lateinit var adapter: DefaultPackingZonesAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = DefaultPackingZoneBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.dropOffButton.isEnabled = false
        handleButtonUI(false)
        setupRecyclerView()
        handleDropOffButton()
    }

    private fun handleButtonUI(buttonEnabled: Boolean) {
        val context = requireContext()
        binding.dropOffButton.apply {
            val (backgroundRes, textColor) = if (!buttonEnabled) {
                R.drawable.grey_rounded_button to context.getColor(R.color.disabled_button_text_color)
            } else {
                R.drawable.orange_rounded_button to Color.WHITE
            }
            background = AppCompatResources.getDrawable(context, backgroundRes)
            setTextColor(textColor)
        }

    }


    private fun setupRecyclerView() {
        adapter = DefaultPackingZonesAdapter(this, requireContext(), showArrow = false)
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
        adapter.submitList(defaultPackingZones)
    }

    private fun handleDropOffButton() {
        binding.dropOffButton.setOnClickListener {
            dismiss()
            onDropOffClick(selectedZone)
        }
    }

    override fun onViewClicked(data: DefaultPackingZoneResultModel) {
        binding.dropOffButton.isEnabled = true
        handleButtonUI(true)
        selectedZone = data
        adapter.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
