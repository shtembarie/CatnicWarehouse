package com.example.catnicwarehouse.Inventory.matchFoundStockYard.presentation.bottomSheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.databinding.ScanOptionsLayoutBinding
import com.example.catnicwarehouse.scan.presentation.enums.ScanOptionEnum
import com.example.shared.local.dataStore.DataStoreManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ScanOptionsInventoryBottomSheet : BottomSheetDialogFragment() {

    private var _binding: ScanOptionsLayoutBinding? = null
    private val binding get() = _binding!!
    private var bottomSheet: ManualInputInventoryBottomSheet? = null
    private var selectedScanOption: ScanOptionEnum = ScanOptionEnum.BARCODE

    @Inject
    lateinit var dataStoreManager: DataStoreManager


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ScanOptionsLayoutBinding.inflate(inflater, container, false)
        binding.buttonBack.setOnClickListener {
            dismiss()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        handleScanOptionsSelectionUI()
    }


    private fun init() {
        val greyBorder =
            ContextCompat.getDrawable(requireContext(), R.drawable.grey_outline_round_border)

        with(binding) {
            barcodeScanButton.background = greyBorder
            scanningCameraButton.background = greyBorder
            manualInputButton.background = greyBorder

            checkBarcodeIcon.isVisible = false
            checkCameraIcon.isVisible = false
            checkManualInputIcon.isVisible = false
        }
    }


    private fun handleScanOptionsSelectionUI() {

        val blackBorder =
            ContextCompat.getDrawable(requireContext(), R.drawable.black_outline_round_border)
        val greyBorder =
            ContextCompat.getDrawable(requireContext(), R.drawable.grey_outline_round_border)


        with(binding) {

            barcodeScanButton.setOnClickListener {
                barcodeScanButton.background = blackBorder
                scanningCameraButton.background = greyBorder
                manualInputButton.background = greyBorder

                checkBarcodeIcon.isVisible = true
                checkCameraIcon.isVisible = false
                checkManualInputIcon.isVisible = false
                selectedScanOption = ScanOptionEnum.BARCODE

            }
            scanningCameraButton.setOnClickListener {
                barcodeScanButton.background = greyBorder
                scanningCameraButton.background = blackBorder
                manualInputButton.background = greyBorder

                checkBarcodeIcon.isVisible = false
                checkCameraIcon.isVisible = true
                checkManualInputIcon.isVisible = false
                selectedScanOption = ScanOptionEnum.CAMERA
            }

            manualInputButton.setOnClickListener {
                barcodeScanButton.background = greyBorder
                scanningCameraButton.background = greyBorder
                manualInputButton.background = blackBorder

                checkBarcodeIcon.isVisible = false
                checkCameraIcon.isVisible = false
                checkManualInputIcon.isVisible = true
                selectedScanOption = ScanOptionEnum.MANUAL
            }

            buttonContinue.setOnClickListener {
                when (selectedScanOption) {
                    ScanOptionEnum.BARCODE -> {
                        lifecycleScope.launch {
                            dataStoreManager.isFirstScan { isFirstScan ->
                                if (isFirstScan) {
                                    launch {
                                        dataStoreManager.setFirstScan(isFirstScan = false)
                                        findNavController().navigate(R.id.scanDeviceInstructionsFragment)
                                        dismiss()
                                    }

                                }
                            }
                        }
                    }

                    ScanOptionEnum.CAMERA -> {}
                    ScanOptionEnum.MANUAL -> openManualInputBottomSheet()
                }
                dismiss()

            }
        }
    }

    private fun openManualInputBottomSheet() {
        bottomSheet = ManualInputInventoryBottomSheet().apply {
            onDismissListener = {
                bottomSheet = null
            }
        }
        bottomSheet?.show(parentFragmentManager, bottomSheet?.tag)
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}