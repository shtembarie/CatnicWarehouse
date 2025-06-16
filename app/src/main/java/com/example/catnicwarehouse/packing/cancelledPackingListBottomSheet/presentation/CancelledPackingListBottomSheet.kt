package com.example.catnicwarehouse.packing.cancelledPackingListBottomSheet.presentation

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.databinding.CancelPackingListBottomSheetBinding
import com.example.catnicwarehouse.databinding.PackingListCommentBottomSheetBinding
import com.example.catnicwarehouse.packing.finalisePackingList.presentation.bottomSheet.CancelPackingListBottomSheet
import com.example.catnicwarehouse.packing.packingItem.presentation.bottomSheetFragment.PackingListCommentBottomSheet
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.cancel_packing_list_bottom_sheet.view.textViewSubtitle
import kotlinx.android.synthetic.main.dialog_error.backButton


@AndroidEntryPoint
class CancelledPackingListBottomSheet(
    private val listener: CancelledPackingListListener
) : BottomSheetDialogFragment() {

    companion object {
        fun newInstance(listener: CancelledPackingListListener): CancelledPackingListBottomSheet {
            return CancelledPackingListBottomSheet(listener).apply {
                // Additional setup if needed
            }
        }
    }

    private var _binding: CancelPackingListBottomSheetBinding? = null
    private val binding get() = _binding!!

    // Define the interface
    interface CancelledPackingListListener {
        fun onChoosePackingZoneButtonFromCancelledPackingListClicked()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = CancelPackingListBottomSheetBinding.inflate(inflater, container, false)
        isCancelable = false
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding){
            textViewTitle.text = getString(R.string.packing_list_cancelled)
            textViewSubtitle.text = getString(R.string.packing_list_cancelled_message)
            buttonBack.visibility = View.GONE
            cancelButton.text = getString(R.string.choose_packing_drop_zone)
        }

        //Cancel button callback
        binding.cancelButton.setOnClickListener {
            listener.onChoosePackingZoneButtonFromCancelledPackingListClicked()
        }



    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
