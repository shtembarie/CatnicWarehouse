package com.example.catnicwarehouse.inventoryNew.matchFound.presentation.bottomsheet

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import com.example.catnicwarehouse.databinding.AddOrOverrideInventoryBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddOrOverrideInventoryBottomSheet  : BottomSheetDialogFragment() {

    companion object {
        private const val ARG_ID = "arg_id"
        const val REQUEST_KEY = "AddOrOverrideInventoryAction"
        const val BUNDLE_KEY = "AddOrOverrideInventoryResult"
        const val BUNDLE_ITEM_ID = "item_id"

        fun newInstance(id: Int): AddOrOverrideInventoryBottomSheet {
            return AddOrOverrideInventoryBottomSheet().apply {
                arguments = Bundle().apply {
                    putInt(ARG_ID, id)
                }
            }
        }
    }

    private var _binding: AddOrOverrideInventoryBottomSheetBinding? = null
    private val binding get() = _binding!!

    private val itemId: Int
        get() = arguments?.getInt(ARG_ID) ?: -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = AddOrOverrideInventoryBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // Handle button click actions
        binding.cancelButton.setOnClickListener {
            parentFragmentManager.setFragmentResult(REQUEST_KEY, bundleOf(BUNDLE_KEY to "cancel", BUNDLE_ITEM_ID to itemId))
            dismiss()
        }

        binding.addInventoryButton.setOnClickListener {
            parentFragmentManager.setFragmentResult(REQUEST_KEY, bundleOf(BUNDLE_KEY to "add", BUNDLE_ITEM_ID to itemId))
            dismiss()
        }

        binding.overrideInventoryButton.setOnClickListener {
            parentFragmentManager.setFragmentResult(REQUEST_KEY, bundleOf(BUNDLE_KEY to "override", BUNDLE_ITEM_ID to itemId))
            dismiss()
        }
        // Set background to transparent
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    override fun onStart() {
        super.onStart()
        // Adjust bottom sheet height and behavior when the dialog is shown
        dialog?.let { dialog ->
            val bottomSheet = dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let {
                val behavior = BottomSheetBehavior.from(it)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                // Set height to 85% of the screen
                it.layoutParams.height = (resources.displayMetrics.heightPixels * 0.55).toInt()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
