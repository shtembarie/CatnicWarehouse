package com.example.catnicwarehouse.packing.finalisePackingList.presentation.bottomSheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.catnicwarehouse.databinding.FinaliseIncompletePackingListBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class FinaliseIncompletePackingListBottomSheet(
    private val listener: FinaliseIncompletePackingListListener
) : BottomSheetDialogFragment() {

    companion object {
        fun newInstance(listener: FinaliseIncompletePackingListListener): FinaliseIncompletePackingListBottomSheet {
            return FinaliseIncompletePackingListBottomSheet(listener).apply {
                // Additional setup if needed
            }
        }
    }

    private var _binding: FinaliseIncompletePackingListBottomSheetBinding? = null
    private val binding get() = _binding!!

    // Define the interface
    interface FinaliseIncompletePackingListListener {
        fun onCloseClicked()
        fun onPauseClicked()
        fun onCancelClicked()
        fun onBackClicked()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FinaliseIncompletePackingListBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Close Button callback
        binding.closeButton.setOnClickListener {
            listener.onCloseClicked()
        }

        // Pause Button callback
        binding.pauseButton.setOnClickListener {
            listener.onPauseClicked()
        }

        //Cancel button callback
        binding.cancelButton.setOnClickListener {
            listener.onCancelClicked()
        }

        // Back Button callback
        binding.buttonBack.setOnClickListener {
            listener.onBackClicked()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
