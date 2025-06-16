package com.example.catnicwarehouse.packing.shippingContainer.presentation.bottomSheet

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.databinding.ShippingContainerDimensionLayoutBinding
import com.example.catnicwarehouse.packing.shippingContainer.presentation.adapter.ShippingContainerTypeAdapter
import com.example.shared.networking.network.packing.model.shippingContainer.shippingContainerTypes.ShippingContainerTypeResponseModelItem
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ShippingContainerDimensionBottomSheet : BottomSheetDialogFragment() {

    private var _binding: ShippingContainerDimensionLayoutBinding? = null
    private val binding get() = _binding!!

    private var selectedContainerType: ShippingContainerTypeResponseModelItem? = null


    companion object {
        private const val ARG_CONTAINER_TYPES = "container_type"

        fun newInstance(selectedContainerType: ShippingContainerTypeResponseModelItem): ShippingContainerDimensionBottomSheet {
            return ShippingContainerDimensionBottomSheet().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_CONTAINER_TYPES, selectedContainerType)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            selectedContainerType = it.getParcelable(ARG_CONTAINER_TYPES)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = ShippingContainerDimensionLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.title.text = selectedContainerType?.description
        setupFormValidation()
        handleCreateButton()
        handleCreateWithoutButton()
    }

    private fun handleCreateWithoutButton() {
        binding.createWithoutButton.setOnClickListener {
            selectedContainerType?.let { type ->
                parentFragmentManager.setFragmentResult(
                    "ShippingContainerTypeSelected",
                    bundleOf("selectedType" to type)
                )
                dismiss()
            }
        }
    }

    private fun handleCreateButton() {
        binding.createButton.setOnClickListener {

            val width = binding.widthText.text?.toString()?.trim()
            val height = binding.heightText.text?.toString()?.trim()
            val depth = binding.depthText.text?.toString()?.trim()
            val netWeight = binding.netWeightItemText.text?.toString()?.trim()
            val totalWeight = binding.totalWeightText.text?.toString()?.trim()


            selectedContainerType?.let { type ->
                parentFragmentManager.setFragmentResult(
                    "ShippingContainerTypeSelected",
                    bundleOf(
                        "selectedType" to type,
                        "width" to width,
                        "height" to height,
                        "depth" to depth,
                        "netWeight" to netWeight,
                        "totalWeight" to totalWeight
                    )

                )
                dismiss()
            }
        }
    }


    private fun setupFormValidation() {


        // Add TextWatchers for each field
        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // Validate the form after text changes
                validateForm()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        // Attach the TextWatcher to each input field
        binding.widthText.addTextChangedListener(textWatcher)
        binding.heightText.addTextChangedListener(textWatcher)
        binding.depthText.addTextChangedListener(textWatcher)
        binding.netWeightItemText.addTextChangedListener(textWatcher)
        binding.totalWeightText.addTextChangedListener(textWatcher)
        validateForm()

    }


    private fun validateForm() {
        // Enable button only if all fields are not null or empty
        val isEnabled = !binding.widthText.text.isNullOrEmpty() &&
                !binding.heightText.text.isNullOrEmpty() &&
                !binding.depthText.text.isNullOrEmpty() &&
                !binding.netWeightItemText.text.isNullOrEmpty() &&
                !binding.totalWeightText.text.isNullOrEmpty()

        handleConfirmButton(isEnabled)
    }

    private fun handleConfirmButton(buttonEnabled: Boolean) {
        val context = requireContext()
        binding.createButton.apply {
            val (backgroundRes, textColor) = if (!buttonEnabled) {
                R.drawable.grey_rounded_button to context.getColor(R.color.disabled_button_text_color)
            } else {
                R.drawable.orange_rounded_button to Color.WHITE
            }
            background = AppCompatResources.getDrawable(context, backgroundRes)
            setTextColor(textColor)
        }
        binding.createButton.isEnabled = buttonEnabled
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
