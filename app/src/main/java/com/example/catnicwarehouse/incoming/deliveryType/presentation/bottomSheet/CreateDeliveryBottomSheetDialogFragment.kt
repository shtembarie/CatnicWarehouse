package com.example.catnicwarehouse.incoming.deliveryType.presentation.bottomSheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.databinding.BottomSheetDeliveryBinding
import com.example.catnicwarehouse.incoming.deliveryType.presentation.sealedClass.DeliveryTypeEvent
import com.example.catnicwarehouse.incoming.deliveryType.presentation.sealedClass.DeliveryTypeViewState
import com.example.catnicwarehouse.incoming.deliveryType.presentation.viewModel.DeliveryTypeViewModel
import com.example.catnicwarehouse.incoming.utils.IncomingConstants
import com.example.catnicwarehouse.shared.presentation.enums.DeliveryType
import com.example.catnicwarehouse.shared.presentation.enums.DeliveryType.*
import com.example.catnicwarehouse.shared.presentation.sealedClasses.SharedEvent
import com.example.catnicwarehouse.shared.presentation.sealedClasses.IncomingSharedViewState
import com.example.catnicwarehouse.shared.presentation.viewModel.SharedViewModelNew
import com.example.shared.networking.network.delivery.model.createDelivery.CreateDeliveryRequestModel
import com.example.shared.utils.BannerBar
import com.example.shared.utils.ProgressBarManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class DeliveryTypeBottomSheet : BottomSheetDialogFragment() {
    private var _binding: BottomSheetDeliveryBinding? = null
    private val binding get() = _binding!!
    private val progressBarManager by lazy { ProgressBarManager(requireActivity()) }
    private val viewModel: DeliveryTypeViewModel by viewModels()
    private val sharedViewModel: SharedViewModelNew by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetDeliveryBinding.inflate(inflater, container, false)
        // Make bottom sheet expand to full screen height on display
        dialog?.setOnShowListener { dialogInterface ->
            val bottomSheetDialog = dialogInterface as BottomSheetDialog
            val bottomSheet = bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout?
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
        handleDeliveryTypeSelectionUI()
//        observeDeliveryTypeFlow()
        observeSharedEvents()
        handleButtonClickListener()
    }

    private fun observeSharedEvents() {
        sharedViewModel.incomingSharedFlow.onEach { state ->
            when (state) {
                IncomingSharedViewState.Empty -> progressBarManager.dismiss()
                is IncomingSharedViewState.Error -> progressBarManager.dismiss()
                IncomingSharedViewState.Loading -> progressBarManager.show()
                else -> progressBarManager.dismiss()
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun handleButtonClickListener() {
        with(binding) {
            buttonContinue.setOnClickListener {
                dialog?.dismiss()
                handleNavigationBasedOnDeliveryType()
//                val createDeliveryRequestModel = CreateDeliveryRequestModel(
//                    vendorId = sharedViewModel.getSupplierInfo()?.id!!,
//                    type = sharedViewModel.getDeliveryType().toString(),
//                    warehouseCode = IncomingConstants.WarehouseParam
//                )
//                viewModel.onEvent(DeliveryTypeEvent.CreateDelivery(createDeliveryRequestModel))
            }

            buttonBack.setOnClickListener {
                dialog?.dismiss()
            }
        }
    }

    private fun handleDeliveryTypeSelectionUI() {

        val blackBorder =
            ContextCompat.getDrawable(requireContext(), R.drawable.black_outline_round_border)
        val greyBorder =
            ContextCompat.getDrawable(requireContext(), R.drawable.grey_outline_round_border)


        with(binding) {

            normalDeliveryButton.setOnClickListener {
                normalDeliveryButton.background = blackBorder
                returnDeliveryButton.background = greyBorder
                checkNormalDeliveryIcon.isVisible = true
                checkReturnDeliveryIcon.isVisible = false
                sharedViewModel.onEvents(SharedEvent.UpdateDeliveryType(PUR))
            }
            returnDeliveryButton.setOnClickListener {
                normalDeliveryButton.background = greyBorder
                returnDeliveryButton.background = blackBorder
                checkNormalDeliveryIcon.isVisible = false
                checkReturnDeliveryIcon.isVisible = true
                sharedViewModel.onEvents(SharedEvent.UpdateDeliveryType(RET))
            }
        }
    }

    private fun init() {
        val blackBorder =
            ContextCompat.getDrawable(requireContext(), R.drawable.black_outline_round_border)
        val greyBorder =
            ContextCompat.getDrawable(requireContext(), R.drawable.grey_outline_round_border)
        val continueButtonDrawable =
            ContextCompat.getDrawable(requireContext(), R.drawable.orange_rounded_button)

        sharedViewModel.onEvents(SharedEvent.UpdateDeliveryType(PUR))

        with(binding) {
            normalDeliveryButton.background = blackBorder
            returnDeliveryButton.background = greyBorder
            checkNormalDeliveryIcon.isVisible = true
            checkReturnDeliveryIcon.isVisible = false
            buttonContinue.background = continueButtonDrawable
        }
    }

//    private fun observeDeliveryTypeFlow() {
//        viewModel.deliveryFlow.onEach { state ->
//            when (state) {
//                is DeliveryTypeViewState.DeliveryCreated -> {
//                    dialog?.dismiss()
//                    progressBarManager.dismiss()
//                    val deliveryId = state.createdDelivery
//                    if (deliveryId != null) {
//                        sharedViewModel.onEvents(
//                            SharedEvent.UpdateDeliveryId(
//                                deliveryId
//                            )
//                        )
//                    }
//
//                }
//
//                DeliveryTypeViewState.Empty -> {
//                    progressBarManager.dismiss()
//                }
//
//                is DeliveryTypeViewState.Error -> {
//                    progressBarManager.dismiss()
//                    state.errorMessage?.let { showErrorBanner(message = state.errorMessage) }
//                }
//
//                DeliveryTypeViewState.Loading -> progressBarManager.show()
//                DeliveryTypeViewState.Reset -> progressBarManager.dismiss()
//            }
//        }.launchIn(viewLifecycleOwner.lifecycleScope)
//    }

    private fun handleNavigationBasedOnDeliveryType() {
        val deliveryType = sharedViewModel.getDeliveryType()
        when(deliveryType){
            PUR ->  findNavController().navigate(R.id.supplierFragment)
            RET ->  findNavController().navigate(R.id.customersFragment)
        }
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}