package com.example.catnicwarehouse.packing.shippingContainer.presentation.bottomSheet

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.databinding.ShippingContainerTypeBottomSheetBinding
import com.example.catnicwarehouse.packing.shippingContainer.presentation.adapter.ShippingContainerTypeAdapter
import com.example.shared.networking.network.packing.model.shippingContainer.shippingContainerTypes.ShippingContainerTypeResponseModelItem
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ShippingContainerTypeBottomSheet : BottomSheetDialogFragment() {

    private var _binding: ShippingContainerTypeBottomSheetBinding? = null
    private val binding get() = _binding!!

    private lateinit var containerTypeAdapter: ShippingContainerTypeAdapter
    private var selectedContainerType: ShippingContainerTypeResponseModelItem? = null
    private var containerTypes: List<ShippingContainerTypeResponseModelItem> = emptyList()

    companion object {
        private const val ARG_CONTAINER_TYPES = "container_types"

        fun newInstance(containerTypes: List<ShippingContainerTypeResponseModelItem>): ShippingContainerTypeBottomSheet {
            return ShippingContainerTypeBottomSheet().apply {
                arguments = Bundle().apply {
                    putParcelableArrayList(ARG_CONTAINER_TYPES, ArrayList(containerTypes))
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            containerTypes = it.getParcelableArrayList(ARG_CONTAINER_TYPES) ?: emptyList()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = ShippingContainerTypeBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonContinue.isEnabled = false
        handleConfirmButton(false)
        initRecyclerView()
        handleConfirmButton()
    }

    private fun initRecyclerView() {
        containerTypeAdapter =
            ShippingContainerTypeAdapter(requireContext(), containerTypes) { selectedType ->
                selectedContainerType = selectedType
                binding.buttonContinue.isEnabled = true
                handleConfirmButton(true)
            }
        binding.containerTypeRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.containerTypeRecyclerView.adapter = containerTypeAdapter
    }

    private fun handleConfirmButton() {
        binding.buttonContinue.setOnClickListener {
            selectedContainerType?.let { type ->
                ShippingContainerDimensionBottomSheet.newInstance(type)
                    .show(parentFragmentManager, "ShippingContainerDimension")
            }
            dismiss()
        }
    }


    private fun handleConfirmButton(buttonEnabled: Boolean) {
        val context = requireContext()
        binding.buttonContinue.apply {
            val (backgroundRes, textColor) = if (!buttonEnabled) {
                R.drawable.grey_rounded_button to context.getColor(R.color.disabled_button_text_color)
            } else {
                R.drawable.orange_rounded_button to Color.WHITE
            }
            background = AppCompatResources.getDrawable(context, backgroundRes)
            setTextColor(textColor)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
