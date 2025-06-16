package com.example.catnicwarehouse.incoming.defective.presentation.bottomSheetFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.databinding.FragmentUnitMeterTypeBottomSheetBinding
import com.example.catnicwarehouse.incoming.amountItem.presentation.adapter.DefectiveReasonAdapter
import com.example.catnicwarehouse.incoming.matchFound.presentation.sealedClass.DefectiveReason
import com.example.catnicwarehouse.shared.presentation.viewModel.SharedViewModelNew
import com.example.shared.utils.ProgressBarManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


@AndroidEntryPoint
class DefectiveReasonsBottomSheetFragment : BottomSheetDialogFragment() {

    companion object {
        private const val DEFECTIVE_REASON = "defective_reason"

        fun newInstance(defectiveReason: DefectiveReason): DefectiveReasonsBottomSheetFragment {
            return DefectiveReasonsBottomSheetFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(DEFECTIVE_REASON, defectiveReason)
                }
            }
        }
    }

    private var _binding: FragmentUnitMeterTypeBottomSheetBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: SharedViewModelNew by activityViewModels()
    private val progressBarManager by lazy { ProgressBarManager(requireActivity()) }
    private lateinit var defectiveReason: DefectiveReason


    interface DefectiveReasonListener {
        fun onDefectiveReasonSelected(defectiveReason: DefectiveReason)
    }

    var listener: DefectiveReasonListener? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            defectiveReason = it.getParcelable(DEFECTIVE_REASON)
                ?: throw IllegalArgumentException("Defective Reason is missing")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUnitMeterTypeBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeSharedEvents()
        init()
        initAdapter(selectedDefectiveReason = defectiveReason)
    }

    private fun observeSharedEvents() {
//        sharedVM.deliverySharedFlow.onEach {  state->
//            when(state){
//                DeliverySharedViewState.Empty -> progressBarManager.dismiss()
//                is DeliverySharedViewState.Error -> progressBarManager.dismiss()
//                DeliverySharedViewState.Loading -> progressBarManager.show()
//                is DeliverySharedViewState.UpdatedValue -> {
//                    progressBarManager.dismiss()
//                    initAdapter(state.deliveryLocalModel.selectedDefectiveReason)
//                }
//
//                is DeliverySharedViewState.NavigateTo -> progressBarManager.dismiss()
//
//                else -> {progressBarManager.dismiss()}
//            }
//        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun init() {
        with(binding) {
            textViewTitle.text = getString(R.string.defective_reasons)
            textViewSubtitle.text = getString(R.string.defective_reason_subtitle)
            textViewFooter.text = getString(R.string.defective_reasons_footer)
        }
    }

    private fun initAdapter(selectedDefectiveReason: DefectiveReason) {
        val adapter =
            DefectiveReasonAdapter(
                defectiveReasons = DefectiveReason.values(),
                selectedDefectiveReason = selectedDefectiveReason
            ) { clickedDefectiveReason ->
                listener?.onDefectiveReasonSelected(defectiveReason = clickedDefectiveReason)
                dismiss()
            }
        binding.rView.adapter = adapter
        binding.rView.layoutManager = LinearLayoutManager(context)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
