package com.example.catnicwarehouse.packing.matchFound.presentation.fragment

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.transition.Visibility
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.base.BaseFragment
import com.example.catnicwarehouse.databinding.FragmentPackingArticlesBinding
import com.example.catnicwarehouse.packing.addPackingItems.presentation.sealedClasses.AddPackingItemsEvent
import com.example.catnicwarehouse.packing.matchFound.presentation.sealedClasses.PackingArticlesEvent
import com.example.catnicwarehouse.packing.matchFound.presentation.sealedClasses.PackingArticlesViewState
import com.example.catnicwarehouse.packing.matchFound.presentation.viewModel.PackingArticlesViewModel
import com.example.catnicwarehouse.packing.packingItem.presentation.sealedClasses.PackingItemsEvent
import com.example.catnicwarehouse.packing.shared.presentation.viewModel.PackingSharedViewModel
import com.example.catnicwarehouse.utils.colorSubstringFromCharacter
import com.example.shared.networking.network.packing.model.amount.PickAmountRequestModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class PackingArticlesFragment : BaseFragment() {

    private var _binding: FragmentPackingArticlesBinding? = null
    private val binding get() = _binding!!
    private val packingSharedViewModel: PackingSharedViewModel by activityViewModels()
    private val viewModel: PackingArticlesViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentPackingArticlesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleHeaderSection()
        handleData()
        handleDoneButtonAction()
        handleAmountSectionAction()
        handleShippingContainerAction()
        observePackingArticlesEvents()
        packingSharedViewModel.selectedPackingItemToPack?.packingListId?.let {
            viewModel.onEvent(PackingArticlesEvent.GetItemsForPacking(it))
            viewModel.onEvent(PackingArticlesEvent.GetPackingItems(it))
        }

    }

    private fun handleShippingContainerAction() {
        binding.containerSection.viewContainer.setOnClickListener {
            val action =
                PackingArticlesFragmentDirections.actionMatchFoundFragment2ToShippingContainerFragment()
            findNavController().navigate(action)
        }
    }

    private fun handleAmountSectionAction() {
        binding.pickupAmountSection.viewContainer.setOnClickListener {
            val action =
                PackingArticlesFragmentDirections.actionMatchFoundFragment2ToAddPackingItemsFragment()
            findNavController().navigate(action)
        }
    }

    private fun handleDoneButtonAction() {

        binding.confirmButton.setOnClickListener {
            navigateToFinalisePackingListFragment()
        }
    }

    private fun isDataValid(): Boolean {
        return ((packingSharedViewModel.selectedPackingItemToPack?.amount ?: 0) >
                ((packingSharedViewModel.selectedPackingItemToPack?.packedAmount ?: 0)
                        + (packingSharedViewModel.selectedPackingItemToPack?.amountToPack ?: 0)))

    }


    private fun handleHeaderSection() {
        binding.deliveryHeader.headerTitle.text = getString(R.string.packing_articles)
        binding.deliveryHeader.toolbarSection.visibility = View.VISIBLE
        binding.deliveryHeader.rightToolbarButton.visibility = View.VISIBLE
        binding.deliveryHeader.rightToolbarButton.visibility = View.GONE
        binding.deliveryHeader.leftToolbarButton.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun handleData() {
        with(binding) {
            //id section
            //match code field is missing from the api response model
            idSection.viewContainer.visibility = View.GONE
            val matchCode = packingSharedViewModel.selectedArticle?.stockYardName ?: "0"
            idSection.titleId.text = matchCode
            if (matchCode.isEmpty().not())
                idSection.decsriptionId.visibility = View.VISIBLE
            else
                idSection.decsriptionId.visibility = View.GONE
            idSection.decsriptionId.text = getString(R.string.code_match_found)
            idSection.toRightBtn.visibility = View.GONE

            //article number
            val articleId = packingSharedViewModel.selectedArticle?.articleId ?: ""
            articleSection.titleId.text = articleId
            articleSection.imgProduct.setImageDrawable(
                AppCompatResources.getDrawable(requireContext(), R.drawable.hash_img)
            )
            articleSection.decsriptionId.visibility = View.VISIBLE
            articleSection.decsriptionId.text = getString(R.string.article_number)
            articleSection.toRightBtn.visibility = View.GONE

            //Available to move section
            val amountAvailable =
                "${
                    packingSharedViewModel.itemsForPacking?.filter { s -> s.articleId == packingSharedViewModel.selectedPackingItemToPack?.articleId }
                        ?.sumOf { s -> s.itemAmount }
                        ?.minus(packingSharedViewModel.itemsForPacking?.filter { s -> s.articleId == packingSharedViewModel.selectedPackingItemToPack?.articleId }
                            ?.sumOf { s -> s.packedAmount }!!) ?: (packingSharedViewModel.selectedPackingItemToPack?.amount?.toInt() ?: 0).minus(
                        packingSharedViewModel.selectedPackingItemToPack?.packedAmount?.toInt() ?: 0
                    ) ?: 0
                }/${packingSharedViewModel.selectedPackingItemToPack?.unitCode ?: ""}"
            val availableAmountSpannableString =
                amountAvailable.colorSubstringFromCharacter('/', Color.LTGRAY)

            availableAmountSection.titleId.text = availableAmountSpannableString
            availableAmountSection.decsriptionId.visibility = View.VISIBLE
            availableAmountSection.decsriptionId.text = getString(R.string.available_items)
            availableAmountSection.imgProduct.setImageDrawable(
                AppCompatResources.getDrawable(requireContext(), R.drawable.avalaible_to_download)
            )
            availableAmountSection.toRightBtn.visibility = View.GONE


            //Pick Up Amount section
            val pickUpAmount =
                "${
                    packingSharedViewModel.itemsForPacking?.filter { s -> s.articleId == packingSharedViewModel.selectedPackingItemToPack?.articleId }
                        ?.sumOf { s -> s.packedAmount } ?: packingSharedViewModel.selectedPackingItemToPack?.amountToPack ?: packingSharedViewModel.selectedPackingItemToPack?.packedAmount ?: 0
                }/${packingSharedViewModel.selectedPackingItemToPack?.unitCode ?: ""}"

            val pickUpAmountSpannableString =
                pickUpAmount.colorSubstringFromCharacter('/', Color.LTGRAY)

            pickupAmountSection.titleId.text = pickUpAmountSpannableString
            pickupAmountSection.decsriptionId.visibility = View.VISIBLE
            pickupAmountSection.decsriptionId.text = getString(R.string.amount_of_items)
            pickupAmountSection.qty.text = getString(R.string.required)
            pickupAmountSection.qty.visibility = View.VISIBLE
            pickupAmountSection.qty.setTextColor(requireContext().getColor(com.example.data.R.color.red_color))
            pickupAmountSection.imgProduct.setImageDrawable(
                AppCompatResources.getDrawable(requireContext(), R.drawable.avalaible_to_download)
            )
            pickupAmountSection.toRightBtn.visibility = View.VISIBLE

            //Storage container section
            val shippingContainerId = packingSharedViewModel.packingItems
                ?.firstOrNull { it.articleId == packingSharedViewModel.selectedArticle?.articleId }
                ?.shippingContainers
                ?.filter {
                    it.packingListId == ((packingSharedViewModel.selectedAssignedPackingListItem?.id)
                        ?: packingSharedViewModel.selectedSearchedPackingListId)
                }
                ?.joinToString(",") { it.shippingContainerId }

            containerSection.titleId.text =
                if (shippingContainerId.isNullOrEmpty()) getString(R.string.none) else shippingContainerId
            containerSection.decsriptionId.visibility = View.VISIBLE
            containerSection.decsriptionId.text = getString(R.string.shipping_container)
            containerSection.qty.text = getString(R.string.required)
            containerSection.qty.visibility = View.VISIBLE
            containerSection.qty.setTextColor(requireContext().getColor(com.example.data.R.color.red_color))
            containerSection.imgProduct.setImageDrawable(
                AppCompatResources.getDrawable(requireContext(), R.drawable.storage_container_icon)
            )

        }
    }

    private fun observePackingArticlesEvents() {
        viewModel.packingArticlesFlow.onEach { state ->
            when (state) {
                PackingArticlesViewState.Empty -> progressBarManager.dismiss()
                is PackingArticlesViewState.Error -> {
                    progressBarManager.dismiss()
                    state.errorMessage?.let { showErrorBanner(message = state.errorMessage) }
                }

                PackingArticlesViewState.Loading -> progressBarManager.show()
                is PackingArticlesViewState.PickAmountResult -> {
                    progressBarManager.dismiss()
                    if (state.isAmountPicked == true) {
                        navigateToFinalisePackingListFragment()
                    }
                }

                PackingArticlesViewState.Reset -> progressBarManager.dismiss()

                is PackingArticlesViewState.GetItemsForPackingResponse -> {
                    progressBarManager.dismiss()
                    packingSharedViewModel.itemsForPacking = state.itemsForPackingItems
                    val packingId = packingSharedViewModel.selectedPackingItemToPack?.packingListId

                    handleData()
                }

                is PackingArticlesViewState.GetPackingItemsResult -> {
                    progressBarManager.dismiss()
                    val packingId = packingSharedViewModel.selectedPackingItemToPack?.packingListId
                    packingSharedViewModel.packingItems =
                        state.packingItems?.items?.filter { s -> s.packingListId == packingId }
                    handleData()

                }

                else -> {
                    progressBarManager.dismiss()

                }
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun navigateToFinalisePackingListFragment() {
        findNavController().popBackStack(R.id.finaliseParentFragment, false)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        viewModel.onEvent(PackingArticlesEvent.Reset)
    }

}