package com.example.catnicwarehouse.defectiveItems.matchFoundFragment.presentation.fragment

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.base.BaseFragment
import com.example.catnicwarehouse.databinding.FragmentMatchFoundDefectiveItemsBinding
import com.example.catnicwarehouse.defectiveItems.matchFoundFragment.presentation.bottomSheet.BottomSheetInfo
import com.example.catnicwarehouse.defectiveItems.matchFoundFragment.presentation.sealedClasses.GetDefectiveArticlesByIdEvent
import com.example.catnicwarehouse.defectiveItems.matchFoundFragment.presentation.sealedClasses.GetDefectiveArticlesByIdViewState
import com.example.catnicwarehouse.defectiveItems.matchFoundFragment.presentation.viewModel.DefectiveArticleByIdViewModel
import com.example.catnicwarehouse.defectiveItems.shared.viewModel.DefectiveArticleSharedViewModel
import com.example.catnicwarehouse.incoming.defective.presentation.bottomSheetFragment.DefectiveReasonsBottomSheetFragment
import com.example.catnicwarehouse.incoming.matchFound.presentation.sealedClass.DefectiveReason
import com.example.catnicwarehouse.tools.bottomSheet.ErrorScanBottomSheet
import com.example.shared.repository.defectiveArticles.GetDefectiveArticleByIdUIModel
import com.example.shared.repository.defectiveArticles.PostDefectiveArticlesModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class MatchFoundDefectiveItemsFragment : BaseFragment() {
    private var _binding: FragmentMatchFoundDefectiveItemsBinding? = null
    private val binding get() = _binding!!
    private val defectiveArticleSharedViewModel: DefectiveArticleSharedViewModel by activityViewModels()
    private val defectiveArticleByIdViewModel: DefectiveArticleByIdViewModel by viewModels()
    private var currentItems: GetDefectiveArticleByIdUIModel? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMatchFoundDefectiveItemsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        openBottomSheet()
        handleHeaderSection()
        cancelButton()
        updateUI()
        observeNavigationResponse()
        handleConfirmButtonAction()
    }

    private fun handleHeaderSection() {
        binding.articleHeader.headerTitle.text = getString(R.string.header_match_found)
        binding.articleHeader.toolbarSection.visibility = View.VISIBLE
        binding.articleHeader.rightToolbarButton.visibility = View.GONE
        binding.articleHeader.leftToolbarButton.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.articleHeader.rightIconButton.visibility = View.VISIBLE

    }
    private fun updateUI(){
        //Section Match found Section
        binding.idSection.titleId.text = defectiveArticleSharedViewModel.selectedDefectiveArticleUIModel?.articleMatchCode ?: defectiveArticleSharedViewModel.selectedWarehouseStockyardInventoryEntry?.articleMatchCode
        binding.idSection.decsriptionId.text = getString(R.string.code_match_found)
        binding.idSection.decsriptionId.setTextColor(context?.let { ContextCompat.getColor(it, R.color.green_text) } ?: Color.BLACK )
        binding.idSection.toRightBtn.visibility = View.GONE
        //Section Location
        binding.idLocation.titleId.text = (defectiveArticleSharedViewModel.selectedDefectiveArticleUIModel?.warehouseStockYardId ?: defectiveArticleSharedViewModel.selectedWarehouseStockyardInventoryEntry?.stockYardId).toString()
        binding.idLocation.decsriptionId.text = getString(R.string.location)
        binding.idLocation.imgProduct.setImageResource(R.drawable.position)
        binding.idLocation.toRightBtn.visibility = View.GONE
        //Section Amount
        if (defectiveArticleSharedViewModel.updadeAmount != null) {
            binding.idAmount.titleId2.visibility = View.VISIBLE
            binding.idAmount.titleId.visibility = View.GONE
            binding.idAmount.titleId2.text = defectiveArticleSharedViewModel.updadeAmount.toString()
        } else if (defectiveArticleSharedViewModel.selectedDefectiveArticleUIModel?.defectiveAmount != null) {
            binding.idAmount.titleId2.visibility = View.VISIBLE
            binding.idAmount.titleId.visibility = View.GONE
            binding.idAmount.titleId2.text = defectiveArticleSharedViewModel.selectedDefectiveArticleUIModel?.defectiveAmount.toString()
        } else {
            binding.idAmount.titleId2.visibility = View.GONE
            binding.idAmount.titleId.visibility = View.VISIBLE
            binding.idAmount.titleId.text = defectiveArticleSharedViewModel.selectedWarehouseStockyardInventoryEntry?.amount.toString()
        }
        binding.idAmount.toRightBtn.visibility = View.VISIBLE
        binding.idAmount.decsriptionId.text = context?.getString(R.string.purchase_order_amount_of_items)
        binding.idAmount.imgProduct.setImageResource(R.drawable.amountsitem)
        binding.idAmount.viewContainer.setOnClickListener {
            binding.root.findNavController().navigate(R.id.action_matchFoundDefectiveItemsFragment_to_defectiveItemsAmountFragment)
        }
        //Section Reason
        if (defectiveArticleSharedViewModel.updatedReason != null) {
            binding.idReason.titleId2.visibility = View.VISIBLE
            binding.idReason.titleId.visibility = View.GONE
            binding.idReason.titleId2.text = defectiveArticleSharedViewModel.updatedReason
        } else if (defectiveArticleSharedViewModel.selectedDefectiveArticleUIModel?.reason != null) {
            binding.idReason.titleId2.visibility = View.GONE
            binding.idReason.titleId.visibility = View.VISIBLE
            binding.idReason.titleId.text = defectiveArticleSharedViewModel.selectedDefectiveArticleUIModel?.reason
        } else {
            binding.idReason.titleId2.visibility = View.GONE
            binding.idReason.titleId.visibility = View.VISIBLE
            binding.idReason.titleId.text = defectiveArticleSharedViewModel.selectedWarehouseStockyardInventoryEntry?.reason ?: ""
        }
        binding.idReason.imgProduct.setImageResource(R.drawable.defectiveitems)
        binding.idReason.decsriptionId.text = getString(R.string.defective_reason)
        binding.idReason.toRightBtn.visibility = View.VISIBLE
        binding.idReason.viewContainer.setOnClickListener {
            val bottomSheetFragment = DefectiveReasonsBottomSheetFragment.newInstance(
                defectiveReason = defectiveArticleSharedViewModel.updatedReason?.let {
                    DefectiveReason.fromValue(it)
                } ?: DefectiveReason.PHYSICAL_DAMAGE
            )
            bottomSheetFragment.listener = object : DefectiveReasonsBottomSheetFragment.DefectiveReasonListener {
                override fun onDefectiveReasonSelected(defectiveReason: DefectiveReason) {
                    defectiveArticleSharedViewModel.saveUpdatedReason(defectiveReason.value)
                    binding.idReason.titleId.text = defectiveReason.value
                }
            }
            bottomSheetFragment.show(childFragmentManager, bottomSheetFragment.tag)
            bottomSheetFragment.dialog?.setOnShowListener { dialogInterface ->
                val bottomSheet = (dialogInterface as BottomSheetDialog)
                    .findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
                bottomSheet?.let {
                    val layoutParams = it.layoutParams
                    layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                    it.layoutParams = layoutParams
                    val behavior = BottomSheetBehavior.from(it)
                    behavior.state = BottomSheetBehavior.STATE_EXPANDED
                }
            }
        }
        //Section Comment
        if (defectiveArticleSharedViewModel.updatedComment != null) {
            binding.idComment.titleId2.visibility = View.VISIBLE
            binding.idComment.titleId.visibility = View.GONE
            binding.idComment.titleId2.text = defectiveArticleSharedViewModel.updatedComment
        } else if (defectiveArticleSharedViewModel.selectedDefectiveArticleUIModel?.comment != null) {
            binding.idComment.titleId2.visibility = View.GONE
            binding.idComment.titleId.visibility = View.VISIBLE
            binding.idComment.titleId.text = defectiveArticleSharedViewModel.selectedDefectiveArticleUIModel?.comment
        } else {
            binding.idComment.titleId2.visibility = View.GONE
            binding.idComment.titleId.visibility = View.VISIBLE
            binding.idComment.titleId.text = defectiveArticleSharedViewModel.selectedWarehouseStockyardInventoryEntry?.comment ?: ""
        }
        binding.idComment.decsriptionId.text = getString(R.string.comments)
        binding.idComment.imgProduct.setImageResource(R.drawable.messages)
        binding.idComment.toRightBtn.visibility = View.VISIBLE
        binding.idComment.viewContainer.setOnClickListener {
            binding.root.findNavController().navigate(R.id.action_matchFoundDefectiveItemsFragment_to_commentDefectiveItemsFragment)
        }
    }
    private fun cancelButton() {
        binding.closeListButton.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.closeListTextView.setOnClickListener {
            findNavController().popBackStack()
        }
    }
    private fun openBottomSheet() {
        binding.articleHeader.rightIconButton.setOnClickListener {
            BottomSheetInfo.showBottomSheetDialog(
                requireContext(),
                onNewIncomingClick = {
                },
                onBackClick = {

                }
            )
        }

    }

    private fun observeNavigationResponse(){
        defectiveArticleByIdViewModel.getDefectiveArticleById.onEach { state ->
        when (state){
            GetDefectiveArticlesByIdViewState.Empty -> {
                progressBarManager.dismiss()
            }
            is GetDefectiveArticlesByIdViewState.Error -> {
                progressBarManager.dismiss()
                state.errorMessage?.let { showErrorBanner(message = state.errorMessage) }
            }
            GetDefectiveArticlesByIdViewState.Loading -> {progressBarManager.show()}
            GetDefectiveArticlesByIdViewState.Reset -> {progressBarManager.dismiss()}
            is GetDefectiveArticlesByIdViewState.DefectiveArticlesById -> {progressBarManager.dismiss()}
            is GetDefectiveArticlesByIdViewState.CreateNewDefectiveItem -> {
                progressBarManager.dismiss()
                state.isSuccess?.let { isSuccess ->
                    if (isSuccess) {
                        defectiveArticleSharedViewModel.reset()
                        findNavController().popBackStack(R.id.defectiveItemsFragment, false)
                        defectiveArticleSharedViewModel.isItemCorrected = true
                    } else {
                        val bottomSheet = ErrorScanBottomSheet.newInstance()
                        bottomSheet.show(parentFragmentManager, bottomSheet.tag)
                    }
                }
            }
        }
        }.launchIn(lifecycleScope)
    }
    private fun handleConfirmButtonAction() {
        if (defectiveArticleSharedViewModel.selectedDefectiveArticleUIModel == null ){
            binding.scanStockyardsButton.visibility = View.VISIBLE
            binding.scanStockyardsButtonGrey.visibility = View.GONE
            binding.scanStockyardsButtonGrey.isEnabled = false
        }else{
            binding.scanStockyardsButton.visibility = View.GONE
            binding.scanStockyardsButtonGrey.visibility = View.VISIBLE
            binding.scanStockyardsButtonGrey.isEnabled = false
        }
        binding.scanStockyardsTextView.setOnClickListener {
            showConfirmationBottomSheet()
        }
        binding.scanStockyardsButton.setOnClickListener {
            showConfirmationBottomSheet()
        }
    }
    private fun showConfirmationBottomSheet() {
        // Create and configure the BottomSheetDialog
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.correct_inventory, null)
        bottomSheetDialog.setContentView(view)

        // Make the view with ID total_amount_section gone
        val totalAmountSection = view.findViewById<View>(R.id.total_amount_section)
        totalAmountSection?.visibility = View.GONE
        val changeIcon = view.findViewById<ImageView>(R.id.barcode_image)
        changeIcon?.setImageResource(R.drawable.infodefective)
        val changeTitle = view.findViewById<TextView>(R.id.title_popup_finalize)
        changeTitle.text = getString(R.string.mark_defective)
        val changeDescription = view.findViewById<TextView>(R.id.description_popup)
        changeDescription.text = getString(R.string.are_you_sure_you_want_to_mark_these_item)

        val locationSection = view.findViewById<View>(R.id.location_section_v)
        val locationDescription = locationSection.findViewById<TextView>(R.id.decsription_id)
        locationDescription.text = (defectiveArticleSharedViewModel.selectedDefectiveArticleUIModel?.warehouseStockYardId ?: defectiveArticleSharedViewModel.selectedWarehouseStockyardInventoryEntry?.stockYardId).toString()
        val locationTitle = locationSection.findViewById<TextView>(R.id.title_id)
        locationTitle.text = getString(R.string.location)
        val rightIcon = locationSection.findViewById<ImageView>(R.id.to_right_btn)
        rightIcon.visibility = View.GONE
        // Set the BottomSheet to be expanded by default
        val bottomSheet =
            bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        bottomSheet?.let {
            val behavior = BottomSheetBehavior.from(it)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.skipCollapsed = true
        }
        // Set click listeners for buttons in the BottomSheet
        val finalizeButton = view.findViewById<TextView>(R.id.newIncomingButton)
        val cancelButton = view.findViewById<TextView>(R.id.backButton)
        // Finalize action (posts the articles)
        finalizeButton.setOnClickListener {
            postArticles()
            bottomSheetDialog.dismiss()
            // Add a 2-second delay before navigating back


        }
        // Cancel action (closes the BottomSheet)
        cancelButton.setOnClickListener {
            bottomSheetDialog.dismiss()
        }
        // Show the BottomSheetDialog
        bottomSheetDialog.show()
    }
    private fun postArticles() {
        // Logic to post articles
        defectiveArticleByIdViewModel.onEvent(
            GetDefectiveArticlesByIdEvent.PostArticles(
                PostDefectiveArticlesModel(
                    warehouseStockYardInventoryEntryId = defectiveArticleSharedViewModel.selectedDefectiveArticleUIModel?.warehouseStockYardInventoryEntryId ?: defectiveArticleSharedViewModel.selectedWarehouseStockyardInventoryEntry?.id,
                    amount = defectiveArticleSharedViewModel.updadeAmount
                        ?: defectiveArticleSharedViewModel.selectedDefectiveArticleUIModel?.defectiveAmount ?: defectiveArticleSharedViewModel.selectedWarehouseStockyardInventoryEntry?.amount?.toInt(),
                    unitCode = defectiveArticleSharedViewModel.selectedDefectiveArticleUIModel?.unitCode ?: defectiveArticleSharedViewModel.selectedWarehouseStockyardInventoryEntry?.unitCode,
                    reason = defectiveArticleSharedViewModel.updatedReason
                        ?: defectiveArticleSharedViewModel.selectedDefectiveArticleUIModel?.reason ?: defectiveArticleSharedViewModel.selectedWarehouseStockyardInventoryEntry?.reason,
                    comment = defectiveArticleSharedViewModel.updatedComment
                        ?: defectiveArticleSharedViewModel.selectedDefectiveArticleUIModel?.comment ?: defectiveArticleSharedViewModel.selectedWarehouseStockyardInventoryEntry?.comment,
                    originType = defectiveArticleSharedViewModel.selectedDefectiveArticleUIModel?.originType ?: "",
                    originObjectId = defectiveArticleSharedViewModel.selectedDefectiveArticleUIModel?.originObjectId ?: ""
                )
            )
        )
    }
    override fun onDestroyView() {
        super.onDestroyView()
        defectiveArticleByIdViewModel.onEvent(GetDefectiveArticlesByIdEvent.Reset)
        _binding = null

    }

}