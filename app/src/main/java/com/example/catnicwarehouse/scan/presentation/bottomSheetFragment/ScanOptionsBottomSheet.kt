package com.example.catnicwarehouse.scan.presentation.bottomSheetFragment


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.databinding.ScanOptionsLayoutBinding
import com.example.catnicwarehouse.scan.presentation.enums.ScanType
import com.example.catnicwarehouse.scan.presentation.enums.ScanOptionEnum
import com.example.catnicwarehouse.scan.presentation.enums.ScanOptionEnum.*
import com.example.catnicwarehouse.utils.isEMDKAvailable
import com.example.shared.local.dataStore.DataStoreManager
import com.example.zebraScanner.presentation.enums.ScannerType
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class ScanOptionsBottomSheet : BottomSheetDialogFragment() {
    companion object {
        private const val ARG_SCAN_TYPE = "scan_type"

        fun newInstance(scanType: ScanType): ScanOptionsBottomSheet =
            ScanOptionsBottomSheet().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_SCAN_TYPE, scanType)
                }
            }
    }

    private var _binding: ScanOptionsLayoutBinding? = null
    private val binding get() = _binding!!

    private var selectedScanOption: ScanOptionEnum = BARCODE

    @Inject
    lateinit var dataStoreManager: DataStoreManager

    private lateinit var scanType: ScanType


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            scanType = it.getParcelable(ARG_SCAN_TYPE)
                ?: throw IllegalArgumentException("ScanType is missing")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = ScanOptionsLayoutBinding.inflate(inflater, container, false)
        // Make bottom sheet expand to full screen height on display
        dialog?.setOnShowListener { dialogInterface ->
            val bottomSheetDialog = dialogInterface as BottomSheetDialog
            val bottomSheet =
                bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout?
            bottomSheet?.let {
                val behavior = BottomSheetBehavior.from(it)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        handleScanOptionsSelectionUI()
        handleInfoIconClickAction()
    }

    private fun handleInfoIconClickAction() {
        binding.scanningInfoIcon.setOnClickListener {
            val bundle = bundleOf("scanType" to scanType.name)
            findNavController().navigate(R.id.scanDeviceInstructionsFragment, bundle)
            dismiss()
        }
    }

    override fun onStart() {
        super.onStart()

        val bottomSheetDialog = dialog as? BottomSheetDialog
        bottomSheetDialog?.let {
            val bottomSheet = it.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            val layoutParams = bottomSheet?.layoutParams
            layoutParams?.height = ViewGroup.LayoutParams.WRAP_CONTENT // Wrap Content
            bottomSheet?.layoutParams = layoutParams
            // Optional: It disable the scrolling within the BottomSheet
            bottomSheet?.setOnTouchListener { _, _ -> true } // Disable touch to scroll
            // Making sure the bottom sheet is expanded
            it.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }


    private fun init() {
        binding.apply {
            val blackBorder =
                ContextCompat.getDrawable(requireContext(), R.drawable.black_outline_round_border)
            val greyBorder =
                ContextCompat.getDrawable(requireContext(), R.drawable.grey_outline_round_border)
            arrayOf(
                barcodeScanButton to blackBorder,
                scanningCameraButton to greyBorder,
                manualInputButton to greyBorder
            ).forEach { (button, border) ->
                button.background = border
            }
            arrayOf(
                checkBarcodeIcon to true,
                checkCameraIcon to false,
                checkManualInputIcon to false
            ).forEach { (icon, isVisible) ->
                icon.isVisible = isVisible
            }
            selectedScanOption = BARCODE

            textViewTitle.text = if (scanType == ScanType.ARTICLE) {
                getString(R.string.start_scanning_articles)
            } else {
                getString(R.string.start_scanning_stockyards)
            }
            // Set the title text based on scanType
            textViewTitle.text = if (scanType == ScanType.ARTICLE) {
                getString(R.string.start_scanning_articles)
            } else {
                getString(R.string.start_scanning_stockyards)
            }
        }
    }

    private fun handleScanOptionsSelectionUI() {
        binding.apply {
            val blackBorder =
                ContextCompat.getDrawable(requireContext(), R.drawable.black_outline_round_border)
            val greyBorder =
                ContextCompat.getDrawable(requireContext(), R.drawable.grey_outline_round_border)

            barcodeScanButton.setOnClickListener {
                arrayOf(
                    barcodeScanButton to blackBorder,
                    scanningCameraButton to greyBorder,
                    manualInputButton to greyBorder
                ).forEach { (button, border) ->
                    button.background = border
                }
                arrayOf(
                    checkBarcodeIcon to true,
                    checkCameraIcon to false,
                    checkManualInputIcon to false
                ).forEach { (icon, isVisible) ->
                    icon.isVisible = isVisible
                }
                selectedScanOption = BARCODE
            }
            scanningCameraButton.setOnClickListener {
                arrayOf(
                    barcodeScanButton to greyBorder,
                    scanningCameraButton to blackBorder,
                    manualInputButton to greyBorder
                ).forEach { (button, border) ->
                    button.background = border
                }
                arrayOf(
                    checkBarcodeIcon to false,
                    checkCameraIcon to true,
                    checkManualInputIcon to false
                ).forEach { (icon, isVisible) ->
                    icon.isVisible = isVisible
                }
                selectedScanOption = CAMERA
            }
            manualInputButton.setOnClickListener {
                arrayOf(
                    barcodeScanButton to greyBorder,
                    scanningCameraButton to greyBorder,
                    manualInputButton to blackBorder
                ).forEach { (button, border) ->
                    button.background = border
                }
                arrayOf(
                    checkBarcodeIcon to false,
                    checkCameraIcon to false,
                    checkManualInputIcon to true
                ).forEach { (icon, isVisible) ->
                    icon.isVisible = isVisible
                }
                selectedScanOption = MANUAL
            }
            buttonContinue.setOnClickListener {
                when (selectedScanOption) {
                    BARCODE -> handleSelectedOption()
                    CAMERA -> handleSelectedOption()
                    MANUAL -> handleSelectedOption()
                }
            }
            buttonBack.setOnClickListener {
                dismiss()
            }
        }
    }

    private fun handleBarcodeOption() {
        lifecycleScope.launch {
            dataStoreManager.isFirstScan { isFirstScan ->
                if (isFirstScan) {

                    launch {
                        val bundle = bundleOf("scanType" to scanType.name)
                        findNavController().navigate(R.id.scanDeviceInstructionsFragment, bundle)
                        dismiss()
                    }
                } else {
                    handleSelectedOption()
                }
            }
        }
    }


    private fun handleSelectedOption() {
        // Set result to trigger the callback in parent fragment
        parentFragmentManager.setFragmentResult(
            "scanOptionBottomSheet",
            bundleOf("scanType" to scanType, "scanOption" to selectedScanOption)
        )
        dismiss()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

