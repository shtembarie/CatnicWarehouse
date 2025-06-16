package com.example.catnicwarehouse.Inventory.matchFoundStockYard.presentation.bottomSheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.catnicwarehouse.databinding.BottomSheetInventoryStockyardBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class InventoryBottomSheet : BottomSheetDialogFragment() {
    private var _binding: BottomSheetInventoryStockyardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = BottomSheetInventoryStockyardBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.buttonBack.setOnClickListener {
            dismiss()
        }
        binding.buttonContinue.setOnClickListener {
            showScanOptionsBottomSheet()
        }

        return view
    }

    private fun showScanOptionsBottomSheet() {
        dismiss()
        val scanOptionsBottomSheet = ScanOptionsInventoryBottomSheet()
        scanOptionsBottomSheet.show(parentFragmentManager, ScanOptionsInventoryBottomSheet::class.java.simpleName)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}