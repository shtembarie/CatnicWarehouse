package com.example.catnicwarehouse.incoming.deliveryDetail.presentation.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.base.BaseFragment
import com.example.catnicwarehouse.databinding.FragmentArticleDetailsBinding
import com.example.catnicwarehouse.incoming.deliveryDetail.presentation.sealedClass.DeliveryDetailsEvent
import com.example.catnicwarehouse.incoming.deliveryDetail.presentation.sealedClass.DeliveryDetailsViewState
import com.example.catnicwarehouse.incoming.deliveryDetail.presentation.viewModel.DeliveryDetailsViewModel
import com.example.catnicwarehouse.shared.presentation.enums.CommentType
import com.example.catnicwarehouse.shared.presentation.sealedClasses.SharedEvent
import com.example.catnicwarehouse.shared.presentation.viewModel.SharedViewModelNew
import com.example.catnicwarehouse.tools.popup.showDeliveryCompleteDialog
import com.example.catnicwarehouse.utils.formatTimestamp
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class DeliveryDetailsFragment : BaseFragment() {

    private var _binding: FragmentArticleDetailsBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: SharedViewModelNew by activityViewModels()
    private val deliveryDetailsViewModel: DeliveryDetailsViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentArticleDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handelHeaderSection()
        observeSharedEvents()
        observeDeliveryDetailsEvents()
        handleCompleteDeliveryButtonAction()
        initViews()
        deliveryDetailsViewModel.onEvent(
            DeliveryDetailsEvent.GetDelivery(
                sharedViewModel.getDeliveryId() ?: ""
            )
        )
    }

    private fun observeSharedEvents() {
//        sharedViewModel.incomingSharedFlow.onEach { state->
//            when(state){
//                IncomingSharedViewState.Empty -> TODO()
//                is IncomingSharedViewState.Error -> TODO()
//                is IncomingSharedViewState.GetDeliveryNoteResult -> TODO()
//                is IncomingSharedViewState.GetDeliveryResponseModelResult -> TODO()
//                is IncomingSharedViewState.IsDeliveryCompletedResult -> TODO()
//                IncomingSharedViewState.Loading -> TODO()
//                is IncomingSharedViewState.SelectedArticleItemModelResult -> TODO()
//                is IncomingSharedViewState.SelectedArticleQtyResult -> TODO()
//                is IncomingSharedViewState.SelectedDefectiveQtyResult -> TODO()
//                is IncomingSharedViewState.SelectedDefectiveReasonResult -> TODO()
//                is IncomingSharedViewState.SelectedDefectiveUnitResult -> TODO()
//                is IncomingSharedViewState.SelectedQuantityUnitResult -> TODO()
//                is IncomingSharedViewState.SelectedSelectedDefectiveCommentResult -> TODO()
//                is IncomingSharedViewState.SelectedWarehouseStockyardIdResult -> TODO()
//                is IncomingSharedViewState.SupplierInfoResult -> TODO()
//            }
//        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun handleCompleteDeliveryButtonAction() {
        binding.buttonContinue.setOnClickListener {
            deliveryDetailsViewModel.onEvent(
                DeliveryDetailsEvent.CompleteDelivery(
                    sharedViewModel.getDeliveryId() ?: ""
                )
            )
        }
    }


    private fun observeDeliveryDetailsEvents() {
        deliveryDetailsViewModel.deliveryDetailsFlow.onEach { state ->
            when (state) {
                is DeliveryDetailsViewState.Delivery -> {
                    progressBarManager.dismiss()
                    sharedViewModel.onEvents(SharedEvent.UpdateGetDeliveryResponseModel(state.delivery))
                }

                DeliveryDetailsViewState.Empty -> progressBarManager.dismiss()
                is DeliveryDetailsViewState.Error -> {
                    progressBarManager.dismiss()
                    state.errorMessage?.let { showErrorBanner(message = state.errorMessage) }
                }

                DeliveryDetailsViewState.Loading -> progressBarManager.show()
                DeliveryDetailsViewState.Reset -> progressBarManager.dismiss()
                is DeliveryDetailsViewState.DeliveryCompleted -> {
                    progressBarManager.dismiss()
                    sharedViewModel.onEvents(SharedEvent.UpdateDeliveryCompleteStatus(state.isDeliveryCompleted))
                    if (state.isDeliveryCompleted == true) {
                        showDeliveryCompleteDialog(
                            requireActivity(),
                            positiveClick = {

                                findNavController().popBackStack(
                                    R.id.deliveryFragment,
                                    inclusive = false
                                )


                            },
                            neutralClick = { requireActivity().finish() })

                    }
                }
            }

        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }


    private fun handelHeaderSection() {
        with(binding) {
            deliveryHeader.headerTitle.text = getString(R.string.delivery)
        }
        binding.deliveryHeader.leftToolbarButton.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun initViews() {
        handleDeliveryCodeSection()
        handleDateSection()
        handleSupplierSection()
        handleArticleSection()
        handleNoteSection()
    }

    private fun handleDeliveryCodeSection() {
        with(binding) {
            deliveryCodeSection.titleId.text = sharedViewModel.getDeliveryId()
            deliveryCodeSection.decsriptionId.text = getString(R.string.delivery_code)
            deliveryCodeSection.decsriptionId.setTextColor(Color.BLACK)
            deliveryCodeSection.imgProduct.setImageDrawable(
                AppCompatResources.getDrawable(requireContext(), R.drawable.barcode_small_icon)
            )
            deliveryCodeSection.toRightBtn.visibility = View.GONE
        }
    }

    private fun handleDateSection() {
        with(binding) {
            dateSection.titleId.text =
                sharedViewModel.getDeliveryResponseModel()?.changedTimestamp?.formatTimestamp()
            dateSection.decsriptionId.text = getString(R.string.date)
            dateSection.decsriptionId.setTextColor(Color.BLACK)
            dateSection.imgProduct.setImageDrawable(
                AppCompatResources.getDrawable(requireContext(), R.drawable.date_icon_img)
            )
            dateSection.toRightBtn.visibility = View.GONE
        }
    }

    private fun handleSupplierSection() {
        with(binding) {
            suppliersSection.viewContainer.visibility = View.GONE
            suppliersSection.titleId.text = getString(R.string.supplier_title)
            suppliersSection.decsriptionId.visibility = View.GONE
            suppliersSection.imgProduct.setImageDrawable(
                AppCompatResources.getDrawable(requireContext(), R.drawable.supplier_icon_imag)
            )
            suppliersSection.viewContainer.setOnClickListener {

            }
        }
    }

    private fun handleArticleSection() {
        with(binding) {
            articleSection.titleId.text = getString(R.string.artikel)
            articleSection.decsriptionId.visibility = View.GONE
            articleSection.qty.visibility = View.GONE
            articleSection.imgProduct.setImageDrawable(
                AppCompatResources.getDrawable(requireContext(), R.drawable.article_icon_img)
            )
            articleSection.viewContainer.setOnClickListener {
                findNavController().popBackStack(R.id.articlesFragment, false)
            }
        }
    }

    private fun handleNoteSection() {
        with(binding) {
            noteSection.titleId.text = getString(R.string.take_a_note)
            noteSection.decsriptionId.visibility = View.GONE
            noteSection.imgProduct.setImageDrawable(
                AppCompatResources.getDrawable(requireContext(), R.drawable.note_icon_img)
            )
            noteSection.viewContainer.setOnClickListener {
                val commentType = CommentType.DELIVERY_NOTE
                val bundle = bundleOf(
                    "commentType" to commentType.name,
                    "comment" to sharedViewModel.getDeliveryNote()
                )
                findNavController().navigate(R.id.commentFragment, bundle)
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}