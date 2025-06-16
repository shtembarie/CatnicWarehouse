package com.example.catnicwarehouse.packing.finalisePackingList.presentation.bottomSheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.catnicwarehouse.databinding.CancelPackingListBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class CancelPackingListBottomSheet(
    private val listener: CancelPackingListListener
) : BottomSheetDialogFragment() {

    companion object {
        fun newInstance(listener: CancelPackingListListener): CancelPackingListBottomSheet {
            return CancelPackingListBottomSheet(listener).apply {
                // Additional setup if needed
            }
        }
    }

    private var _binding: CancelPackingListBottomSheetBinding? = null
    private val binding get() = _binding!!

    // Define the interface
    interface CancelPackingListListener {
        fun onCancelButtonFromCancelPackingListClicked()
        fun onBackButtonFromCancelPackingListClicked()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = CancelPackingListBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        //Cancel button callback
        binding.cancelButton.setOnClickListener {
            listener.onCancelButtonFromCancelPackingListClicked()
        }

        // Back Button callback
        binding.buttonBack.setOnClickListener {
            listener.onBackButtonFromCancelPackingListClicked()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
