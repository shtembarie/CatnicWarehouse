package com.example.catnicwarehouse.tools.bottomSheet

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.example.catnicwarehouse.databinding.DialogSuccessScanPopupBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SuccessScanBottomSheet : BottomSheetDialogFragment() {

    companion object {
        private const val ARG_TITLE_TEXT = "title_text"
        private const val ARG_DESCRIPTION_TEXT = "description_text"
        private const val ARG_BUTTON1_TEXT = "button1_text"
        private const val ARG_BUTTON2_TEXT = "button2_text"
        private const val ARG_ICON_VISIBILITY = "icon_visibility"

        fun newInstance(
            titleText: String,
            descriptionText: String,
            isIconVisible:Boolean = true,
            button1Text: String,
            button2Text: String,
            button1Callback: () -> Unit = {},
            button2Callback: () -> Unit = {}
        ): SuccessScanBottomSheet {
            return SuccessScanBottomSheet().apply {
                arguments = Bundle().apply {
                    putString(ARG_TITLE_TEXT, titleText)
                    putString(ARG_DESCRIPTION_TEXT, descriptionText)
                    putString(ARG_BUTTON1_TEXT, button1Text)
                    putString(ARG_BUTTON2_TEXT, button2Text)
                    putBoolean(ARG_ICON_VISIBILITY, isIconVisible)
                }
                // Set the callbacks
                this.button1Callback = button1Callback
                this.button2Callback = button2Callback
            }
        }
    }

    private var _binding: DialogSuccessScanPopupBinding? = null
    private val binding get() = _binding!!

    private lateinit var titleText: String
    private lateinit var descriptionText: String
    private lateinit var button1Text: String
    private lateinit var button2Text: String
    private var isIconVisible: Boolean = true

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
            isIconVisible = it.getBoolean(ARG_ICON_VISIBILITY)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = DialogSuccessScanPopupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set the values in the layout
        binding.scannedNumber.text = titleText
        binding.descriptionPopup.text = descriptionText
        binding.scanBtn.text = button1Text
        binding.scanAgainBtn.text = button2Text
        binding.barcodeImage.isVisible = isIconVisible

        // Handle button click actions
        binding.scanBtn.setOnClickListener {
            dismiss()
            button1Callback?.invoke()
        }

        binding.scanAgainBtn.setOnClickListener {
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
                //setting the screen to show all the buttons and text
                it.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                // Set height to 85% of the screen
                //it.layoutParams.height = (resources.displayMetrics.heightPixels * 0.55).toInt()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Avoid memory leaks
    }
}
