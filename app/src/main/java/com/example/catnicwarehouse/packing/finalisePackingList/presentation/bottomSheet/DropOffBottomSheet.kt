package com.example.catnicwarehouse.packing.finalisePackingList.presentation.bottomSheet

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.catnicwarehouse.databinding.DropOffBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DropOffBottomSheet : BottomSheetDialogFragment() {

    companion object {
        private const val ARG_TITLE_TEXT = "title_text"
        private const val ARG_DESCRIPTION_TEXT = "description_text"
        private const val ARG_BUTTON1_TEXT = "button1_text"
        private const val ARG_BUTTON2_TEXT = "button2_text"

        fun newInstance(
            titleText: String,
            descriptionText: String,
            button1Text: String,
            button2Text: String,
            button1Callback: () -> Unit = {},
            button2Callback: () -> Unit = {}
        ): DropOffBottomSheet {
            return DropOffBottomSheet().apply {
                arguments = Bundle().apply {
                    putString(ARG_TITLE_TEXT, titleText)
                    putString(ARG_DESCRIPTION_TEXT, descriptionText)
                    putString(ARG_BUTTON1_TEXT, button1Text)
                    putString(ARG_BUTTON2_TEXT, button2Text)
                }
                // Set the callbacks
                this.button1Callback = button1Callback
                this.button2Callback = button2Callback
            }
        }
    }

    private var _binding: DropOffBottomSheetBinding? = null
    private val binding get() = _binding!!

    private lateinit var titleText: String
    private lateinit var descriptionText: String
    private lateinit var button1Text: String
    private lateinit var button2Text: String

    // Callbacks for button actions
    private var button1Callback: (() -> Unit)? = null
    private var button2Callback: (() -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            titleText = it.getString(ARG_TITLE_TEXT) ?: ""
            descriptionText = it.getString(ARG_DESCRIPTION_TEXT) ?: ""
            button1Text = it.getString(ARG_BUTTON1_TEXT) ?: ""
            button2Text = it.getString(ARG_BUTTON2_TEXT) ?: ""
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = DropOffBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set the values in the layout
        binding.scannedNumber.text = titleText
        binding.descriptionPopup.text = descriptionText
        binding.dropOffButton.text = button1Text
        binding.backButton.text = button2Text

        // Handle button click actions
        binding.dropOffButton.setOnClickListener {
            dismiss()
            button1Callback?.invoke()
        }

        binding.backButton.setOnClickListener {
            dismiss()
            button2Callback?.invoke()
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
