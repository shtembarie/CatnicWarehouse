package com.example.catnicwarehouse.packing.packingItem.presentation.bottomSheetFragment

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.catnicwarehouse.databinding.DropOffBottomSheetBinding
import com.example.catnicwarehouse.databinding.PackingListCommentBottomSheetBinding
import com.example.catnicwarehouse.packing.finalisePackingList.presentation.bottomSheet.DropOffBottomSheet
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PackingListCommentBottomSheet  : BottomSheetDialogFragment() {

    companion object {
        private const val ARG_DESCRIPTION_TEXT = "description_text"

        fun newInstance(
            descriptionText: String,
        ): PackingListCommentBottomSheet {
            return PackingListCommentBottomSheet().apply {
                arguments = Bundle().apply {
                    putString(ARG_DESCRIPTION_TEXT, descriptionText)
                }
            }
        }
    }

    private var _binding: PackingListCommentBottomSheetBinding? = null
    private val binding get() = _binding!!

    private lateinit var descriptionText: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            descriptionText = it.getString(ARG_DESCRIPTION_TEXT) ?: ""
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = PackingListCommentBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set the values in the layout
        binding.descriptionPopup.text = descriptionText


        // Handle button click actions
        binding.backButton.setOnClickListener {
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
        _binding = null // Avoid memory leaks
    }
}
