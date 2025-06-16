package com.example.catnicwarehouse.Inventory.matchFoundStockYard.presentation.bottomSheet

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.catnicwarehouse.Inventory.stockyards.presentation.viewModel.InventoryViewModel
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.databinding.FragmentManualInput2Binding
import com.example.catnicwarehouse.scan.presentation.viewModel.ManualInputViewModel
import com.example.shared.utils.BannerBar
import com.example.shared.utils.ProgressBarManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job

@AndroidEntryPoint
class ManualInputInventoryBottomSheet : BottomSheetDialogFragment() {

    var onDismissListener: (() -> Unit)? = null

    private var _binding: FragmentManualInput2Binding? = null
    private val binding get() = _binding!!
    private var observeSharedEventsJob: Job? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentManualInput2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
    private fun showErrorBanner(message: String, displayDuration: Long = 2000) {
        BannerBar.build(requireActivity())
            .setTitle(message)
            .setLayoutGravity(BannerBar.TOP)
            .setBackgroundColor(R.color.red)
            .setDuration(displayDuration)
            .setSwipeToDismiss(true)
            .show()
    }
    override fun onStart() {
        super.onStart()
        val bottomSheet =
            dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        bottomSheet?.let {
            val behavior = BottomSheetBehavior.from(it)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            it.layoutParams.height = (resources.displayMetrics.heightPixels * 0.55).toInt()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        observeSharedEventsJob?.cancel()
        onDismissListener?.invoke()
    }


}