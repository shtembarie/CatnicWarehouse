package com.example.catnicwarehouse.inventoryNew.matchFound.presentation.fragment

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.base.BaseFragment
import com.example.catnicwarehouse.databinding.FragmentMatchBinding
import com.example.catnicwarehouse.inventoryNew.matchFound.presentation.bottomsheet.AddOrOverrideInventoryBottomSheet
import com.example.catnicwarehouse.inventoryNew.matchFound.presentation.sealedClasses.MatchFoundEvent
import com.example.catnicwarehouse.inventoryNew.matchFound.presentation.sealedClasses.MatchFoundViewState
import com.example.catnicwarehouse.inventoryNew.matchFound.presentation.viewModel.MatchFoundViewModel
import com.example.catnicwarehouse.inventoryNew.shared.viewModel.InventorySharedViewModel
import com.example.catnicwarehouse.tools.popup.showExitDialog
import com.example.shared.repository.inventory.model.InventorizeItemRequestModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


@AndroidEntryPoint
class MatchFragment : BaseFragment() {

    private var _binding: FragmentMatchBinding? = null
    private val binding get() = _binding!!
    private val inventorySharedViewModel: InventorySharedViewModel by activityViewModels()
    private val matchFoundViewModel: MatchFoundViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inventorySharedViewModel.updatedUnitCode = null
        inventorySharedViewModel.updatedAmount = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMatchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleHeaderSection()
        populateUIWithData()
        handleClickActions()
        handleResultBackFromAddOrOverrideInventoryBottomSheet()
        handleResultBackFromDuplicateInventoryItems()
        observeMatchFoundEvents()
    }

    private fun handleResultBackFromDuplicateInventoryItems() {
        parentFragmentManager.setFragmentResultListener(
            "conflictItemSelected",
            viewLifecycleOwner
        ) { _, bundle ->
            inventorySharedViewModel.conflictingInventoryitems = null
            val itemId = bundle.getInt("item_id")
            val requestBody = InventorizeItemRequestModel(
                actualStock = inventorySharedViewModel.updatedAmount
                    ?: inventorySharedViewModel.selectedInventoryItem?.actualStock
                    ?: inventorySharedViewModel.selectedArticle?.amount?.toInt()
                    ?: 0,
                actualUnitCode = inventorySharedViewModel.updatedUnitCode
                    ?: inventorySharedViewModel.selectedInventoryItem?.actualUnitCode
                    ?: inventorySharedViewModel.selectedInventoryItem?.targetUnitCode
                    ?: inventorySharedViewModel.selectedArticle?.unitCode
                    ?: "",
                articleId = inventorySharedViewModel.selectedInventoryItem?.articleId
                    ?: inventorySharedViewModel.selectedArticle?.articleId
                    ?: "",
                comment = inventorySharedViewModel.selectedInventoryItem?.comment
                    ?: inventorySharedViewModel.selectedArticle?.comment
                    ?: "",
                warehouseStockYardId = inventorySharedViewModel.selectedInventoryItem?.warehouseStockYardId
                    ?: inventorySharedViewModel.selectedArticle?.stockYardId
                    ?: 0,
                inventoryItemId = itemId
            )
            matchFoundViewModel.onEvent(
                MatchFoundEvent.InventorizeItem(
                    id = inventorySharedViewModel.selectedInventoryId,
                    inventorizeItemRequestModel = requestBody
                )
            )

        }

    }

    private fun observeMatchFoundEvents() {
        matchFoundViewModel.matchFoundFlow.onEach { state ->
            when (state) {
                MatchFoundViewState.Empty -> progressBarManager.dismiss()
                is MatchFoundViewState.Error -> {
                    progressBarManager.dismiss()
                    state.errorMessage?.let { showErrorBanner(it) }
                }

                is MatchFoundViewState.ItemInventorized -> {
                    progressBarManager.dismiss()
                    val hasConflict = state.result?.conflict
                    if (hasConflict == false) {
                        findNavController().popBackStack(R.id.articlesFragment2, inclusive = false)
                    } else if (hasConflict == true) {
                        val conflicts = state.result.conflicts
                        //Already inventoried
                        if (conflicts.alreadyInventoried) {
                            val conflictedInventoryItems = conflicts.inventoryItems
                            if (conflictedInventoryItems.isNotEmpty()) {
                                AddOrOverrideInventoryBottomSheet.newInstance(
                                    state.clickedInventoryItemId ?: -1
                                ).show(childFragmentManager, "AddOrOverrideInventory")
                            }
                        }
                        // Duplicate
                        else if (conflicts.duplicate) {
                            val conflictedInventoryItems = conflicts.inventoryItems
                            inventorySharedViewModel.conflictingInventoryitems =
                                conflictedInventoryItems
                            if (conflictedInventoryItems.isNotEmpty()) {
                                val action =
                                    MatchFragmentDirections.actionMatchFragmentToArticlesFragment2()
                                findNavController().navigate(action)
                            }
                        }
                        Toast.makeText(requireContext(), "has conflicts", Toast.LENGTH_SHORT).show()
                    }
                }

                MatchFoundViewState.Loading -> progressBarManager.show()
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun handleResultBackFromAddOrOverrideInventoryBottomSheet() {
        childFragmentManager.setFragmentResultListener(
            AddOrOverrideInventoryBottomSheet.REQUEST_KEY,
            viewLifecycleOwner
        ) { _, bundle ->

            val requestBody = InventorizeItemRequestModel(
                actualStock = inventorySharedViewModel.updatedAmount
                    ?: inventorySharedViewModel.selectedInventoryItem?.actualStock
                    ?:inventorySharedViewModel.selectedArticle?.amount?.toInt()
                    ?: 0,
                actualUnitCode = inventorySharedViewModel.updatedUnitCode
                    ?: inventorySharedViewModel.selectedInventoryItem?.actualUnitCode
                    ?: inventorySharedViewModel.selectedInventoryItem?.targetUnitCode
                    ?:inventorySharedViewModel.selectedArticle?.unitCode
                    ?: "",
                articleId = inventorySharedViewModel.selectedInventoryItem?.articleId
                    ?:inventorySharedViewModel.selectedArticle?.articleId
                    ?: "",
                comment = inventorySharedViewModel.selectedInventoryItem?.comment
                    ?:inventorySharedViewModel.selectedArticle?.comment
                    ?: "",
                warehouseStockYardId = inventorySharedViewModel.selectedInventoryItem?.warehouseStockYardId
                    ?:inventorySharedViewModel.selectedArticle?.stockYardId
                    ?: 0
            )


            val conflictedInventoryItemId = inventorySharedViewModel.selectedInventoryItem?.id
//                bundle.getInt(AddOrOverrideInventoryBottomSheet.BUNDLE_ITEM_ID, -1)

            val updateRequestBody =
                when (bundle.getString(AddOrOverrideInventoryBottomSheet.BUNDLE_KEY)) {
                    "add" -> {
                        requestBody.copy(
                            addInventory = true,
                            inventoryItemId = conflictedInventoryItemId
                        )
                    }

                    "override" -> {
                        requestBody.copy(
                            overrideInventory = true,
                            inventoryItemId = conflictedInventoryItemId
                        )
                    }

                    "cancel" -> {
                        null
                    }

                    else -> {
                        null
                    }
                }

            updateRequestBody?.let {
                matchFoundViewModel.onEvent(
                    MatchFoundEvent.InventorizeItem(
                        id = inventorySharedViewModel.selectedInventoryId,
                        inventorizeItemRequestModel = it
                    )
                )
            }
        }


    }

    private fun handleClickActions() {
        //description
        binding.articleDescription.viewContainer.setOnClickListener {
            showDescriptionPopup()
        }
        //amount
        binding.quantitySection.viewContainer.setOnClickListener {
            val action = MatchFragmentDirections.actionMatchFragmentToDeliveryFragment2()
            findNavController().navigate(action)
        }
        //comment
        binding.commentSection.viewContainer.setOnClickListener {
            val action = MatchFragmentDirections.actionMatchFragmentToCommentFragment()
            findNavController().navigate(action)
        }

        binding.confirmButton.setOnClickListener {
            matchFoundViewModel.onEvent(
                MatchFoundEvent.InventorizeItem(
                    id = inventorySharedViewModel.selectedInventoryId,
                    inventorizeItemRequestModel = InventorizeItemRequestModel(
                        actualStock = inventorySharedViewModel.updatedAmount
                            ?: inventorySharedViewModel.selectedInventoryItem?.actualStock
                            ?: inventorySharedViewModel.selectedArticle?.amount?.toInt()
                            ?: 0,
                        actualUnitCode = inventorySharedViewModel.updatedUnitCode
                            ?: inventorySharedViewModel.selectedInventoryItem?.actualUnitCode
                            ?: inventorySharedViewModel.selectedInventoryItem?.targetUnitCode
                            ?: inventorySharedViewModel.selectedArticle?.unitCode
                            ?: "",
                        articleId = inventorySharedViewModel.selectedInventoryItem?.articleId
                            ?: inventorySharedViewModel.selectedArticle?.articleId
                            ?: "",
                        comment = inventorySharedViewModel.selectedInventoryItem?.comment
                            ?: inventorySharedViewModel.selectedArticle?.comment
                            ?: "",
                        warehouseStockYardId = inventorySharedViewModel.selectedInventoryItem?.warehouseStockYardId
                            ?: inventorySharedViewModel.selectedArticle?.stockYardId
                            ?: 0
                    )
                )
            )
        }
    }

    private fun populateUIWithData() {
        val currentInventoryItem = inventorySharedViewModel.selectedInventoryItem
        val selectedArticle = inventorySharedViewModel.selectedArticle



        binding.idSection.titleId.text =
            currentInventoryItem?.articleMatchcode ?: selectedArticle?.articleMatchCode
        binding.idSection.decsriptionId.apply {
            text = getString(R.string.code_match_found)
            setTextColor(ContextCompat.getColor(context, R.color.light_green))
        }
        binding.idSection.toRightBtn.visibility = View.GONE

        binding.articleDescription.titleId.text =
            currentInventoryItem?.articleDescription ?: selectedArticle?.articleDescription
        binding.articleDescription.decsriptionId.text = getString(R.string.article_description)
        binding.articleDescription.imgProduct.setImageResource(R.drawable.article_description)

        binding.quantitySection.titleId.text =
            (inventorySharedViewModel.updatedAmount ?: currentInventoryItem?.actualStock
            ?: selectedArticle?.amount ?: "").toString()

        binding.quantitySection.decsriptionId.text = getString(R.string.counted_in_stock)
        binding.quantitySection.imgProduct.setImageResource(R.drawable.countedinstock)

        binding.commentSection.titleId.text =
            currentInventoryItem?.comment ?: selectedArticle?.comment ?: ""
        binding.commentSection.decsriptionId.text = getString(R.string.leave_your_note)
        binding.commentSection.imgProduct.setImageResource(R.drawable.leaveyournote)


        binding.articleSection.toRightBtn.visibility = View.GONE
    }

    private fun handleHeaderSection() {
        binding.articleHeader.headerTitle.text =
            (inventorySharedViewModel.selectedInventoryItem?.articleId
                ?: inventorySharedViewModel.selectedArticle?.articleId
                ?: "").toString()
        binding.articleHeader.toolbarSection.visibility = View.VISIBLE
        binding.articleHeader.rightToolbarButton.visibility = View.GONE
        binding.articleHeader.leftToolbarButton.setOnClickListener {
            findNavController().popBackStack(R.id.articlesFragment2, inclusive = false)
        }
    }


    private fun showDescriptionPopup() {

        showExitDialog(
            activity = requireActivity(),
            showIcon = false,
            positiveButtonText = getString(R.string.copy),
            title = inventorySharedViewModel.selectedInventoryItem?.articleMatchcode
                ?: inventorySharedViewModel.selectedArticle?.articleMatchCode
                ?: getString(R.string.exit),
            description = inventorySharedViewModel.selectedInventoryItem?.articleDescription
                ?: inventorySharedViewModel.selectedArticle?.articleDescription
                ?: getString(
                    R.string.leave_desc
                ),
            positiveClick = {
                val clipboard =
                    requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val combinedText =
                    "${inventorySharedViewModel.selectedInventoryItem?.articleMatchcode ?: inventorySharedViewModel.selectedArticle?.articleMatchCode}\n\n${inventorySharedViewModel.selectedInventoryItem?.articleDescription ?: inventorySharedViewModel.selectedArticle?.articleDescription}"
                val clip = ClipData.newPlainText("Article Text", combinedText)
                clipboard.setPrimaryClip(clip)
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        matchFoundViewModel.onEvent(MatchFoundEvent.Reset)
    }

}