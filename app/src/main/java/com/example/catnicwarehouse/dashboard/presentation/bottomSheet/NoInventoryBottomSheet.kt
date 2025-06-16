package com.example.catnicwarehouse.dashboard.presentation.bottomSheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.catnicwarehouse.databinding.ItemOutOfStockBinding
import com.example.catnicwarehouse.databinding.NoInventoryBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class NoInventoryBottomSheet : BottomSheetDialogFragment() {

    companion object {
        fun newInstance(): NoInventoryBottomSheet {
            return NoInventoryBottomSheet()
        }
    }

    private var _binding: NoInventoryBottomSheetBinding? = null
    private val binding get() = _binding!!



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = NoInventoryBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Back Button callback
        binding.buttonBack.setOnClickListener {
            dismiss()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
