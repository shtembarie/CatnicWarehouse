package com.example.catnicwarehouse.tools.bottomSheet

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.databinding.DialogErrorScanPopUpBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ErrorScanBottomSheet : BottomSheetDialogFragment() {

    companion object {
        private const val ARG_TITLE = "title_text"
        private const val ARG_DESCRIPTION = "description_text"
        private const val ARG_BUTTON_TEXT = "button_text"

        fun newInstance(
            titleText: String="",
            descriptionText: String="",
            buttonText: String="",
            button1Callback: () -> Unit = {},
        ): ErrorScanBottomSheet =
            ErrorScanBottomSheet().apply {
                arguments = Bundle().apply {
                    putString(ARG_TITLE, titleText)
                    putString(ARG_DESCRIPTION, descriptionText)
                    putString(ARG_BUTTON_TEXT, buttonText)
                }
                this.button1Callback = button1Callback
            }
    }

    private var _binding: DialogErrorScanPopUpBinding? = null
    private val binding get() = _binding!!

    private lateinit var titleText: String
    private lateinit var descriptionText: String
    private lateinit var buttonText: String

    private var button1Callback: (() -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            titleText = it.getString(ARG_TITLE) ?: getString(R.string.unknown_bar_code)
            descriptionText = it.getString(ARG_DESCRIPTION) ?: getString(R.string.popup_description)
            buttonText = it.getString(ARG_BUTTON_TEXT) ?: getString(R.string.scan_again)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogErrorScanPopUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set the custom title, description, and button text
        binding.titlePopup.text = titleText.ifEmpty { getString(R.string.unknown_bar_code) }
        binding.descriptionPopup.text = descriptionText.ifEmpty { getString(R.string.popup_description) }
        binding.scanBtn.text = buttonText.ifEmpty { getString(R.string.scan_again) }

        // Handle button click
        binding.scanBtn.setOnClickListener {
            dismiss()
            button1Callback?.invoke()
        }

        // Set background to transparent
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    override fun onStart() {
        super.onStart()
        // Expand the bottom sheet and set its height
        dialog?.let { dialog ->
            val bottomSheet = dialog.findViewById<View>(
                com.google.android.material.R.id.design_bottom_sheet
            )
            bottomSheet?.let {
                val behavior = BottomSheetBehavior.from(it)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                it.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                //it.layoutParams.height = (resources.displayMetrics.heightPixels * 0.55).toInt()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Avoid memory leaks
    }
}
