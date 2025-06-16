package com.example.catnicwarehouse.packing.packingItem.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.Visibility
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.base.BaseFragment
import com.example.catnicwarehouse.databinding.FragmentPackingItemsBinding
import com.example.catnicwarehouse.packing.finalisePackingList.presentation.bottomSheet.DropOffBottomSheet
import com.example.catnicwarehouse.packing.finalisePackingList.presentation.sealedClasses.FinalisePackingEvent
import com.example.catnicwarehouse.packing.packingItem.presentation.adapter.PackingItemsAdapter
import com.example.catnicwarehouse.packing.packingItem.presentation.adapter.PackingItemsAdapterInteraction
import com.example.catnicwarehouse.packing.packingItem.presentation.bottomSheetFragment.PackingListCommentBottomSheet
import com.example.catnicwarehouse.packing.packingItem.presentation.sealedClasses.PackingItemsEvent
import com.example.catnicwarehouse.packing.packingItem.presentation.sealedClasses.PackingItemsViewState
import com.example.catnicwarehouse.packing.packingItem.presentation.viewModel.PackingItemsViewModel
import com.example.catnicwarehouse.packing.packingList.presentation.sealedClasses.PackingListEvent
import com.example.catnicwarehouse.packing.shared.PackingItemStatus
import com.example.catnicwarehouse.packing.shared.presentation.viewModel.PackingSharedViewModel
import com.example.catnicwarehouse.tools.popup.showExitDialog
import com.example.shared.local.dataStore.DataStoreManager
import com.example.shared.networking.network.packing.model.packingItem.PackingItem
import com.example.shared.networking.network.packing.model.startPacking.CancelPackingRequestModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class PackingItemsFragment : BaseFragment(), PackingItemsAdapterInteraction {

    private var _binding: FragmentPackingItemsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PackingItemsViewModel by viewModels()
    private val packingSharedViewModel: PackingSharedViewModel by activityViewModels()
    private lateinit var packingItemsAdapter: PackingItemsAdapter

    private val args: PackingItemsFragmentArgs by navArgs()
    private var packingId: String? = null
    private var isForRelatedPackingItems: Boolean = false
    private var comment: String? = ""
    @Inject
    lateinit var dataStoreManager: DataStoreManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentPackingItemsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        packingId = args.packingId
        isForRelatedPackingItems = args.isForRelatedPackingList
        handleHeaderSection()
        setUpAdapter()
        observePackingItemsEvents()
        handleStartPackingButtonAction()
        handleCommentIconAction()
        showBannerAccordingToStatus()
        viewModel.onEvent(PackingItemsEvent.GetPackingListComment(id = packingId))
        viewModel.onEvent(PackingItemsEvent.GetPackingItems(id = packingId))
    }

    private fun handleCommentIconAction() {
        binding.deliveryHeader.rightToolbarButton.setOnClickListener {
            openCommentBottomSheet()
        }
    }

    private fun handleStartPackingButtonAction() {

        if (isForRelatedPackingItems
            || packingSharedViewModel.selectedPackingListItem?.status == PackingItemStatus.CLS.name
            || packingSharedViewModel.selectedPackingListItem?.status == PackingItemStatus.DLV.name
            || packingSharedViewModel.selectedPackingListItem?.status == PackingItemStatus.CNC.name
            || packingSharedViewModel.selectedPackingListItem?.status == PackingItemStatus.CNR.name
        ) {
            binding.startPackingButton.visibility = View.GONE
            binding.startPackingTextView.visibility = View.GONE
        } else {
            binding.startPackingButton.visibility = View.VISIBLE
            binding.startPackingTextView.visibility = View.VISIBLE
        }

        if (packingSharedViewModel.selectedPackingListItem?.status == PackingItemStatus.OPE.name
            ||
            packingSharedViewModel.selectedPackingListItem?.status == PackingItemStatus.PAU.name
        ) {
            binding.startPackingTextView.text = getString(R.string.start_packing)
        } else {
            binding.startPackingTextView.text = getString(R.string.continue_packing)
        }
        binding.startPackingButton.setOnClickListener {
            startPackingAction()
        }

        binding.startPackingTextView.setOnClickListener {
            startPackingAction()
        }
    }

    private fun startPackingAction() {
        if (packingSharedViewModel.selectedPackingListItem?.status == PackingItemStatus.OPE.name
            ||
            packingSharedViewModel.selectedPackingListItem?.status == PackingItemStatus.PAU.name
        ) {
            viewModel.onEvent(
                PackingItemsEvent.StartPacking(
                    id = packingId
                )
            )
        } else {
            val action =
                PackingItemsFragmentDirections.actionPackingItemsFragmentToFinalisePackingFragment()
            packingId?.let { action.packingId = it }
            findNavController().navigate(action)
        }

    }

    private fun handleHeaderSection() {
        var header = packingId
        if (isForRelatedPackingItems)
            header = getString(R.string.related_packing, packingId)
        binding.deliveryHeader.headerTitle.text = header
        binding.deliveryHeader.toolbarSection.visibility = View.VISIBLE
        binding.deliveryHeader.rightToolbarButton.visibility = View.VISIBLE
        binding.deliveryHeader.rightToolbarButton.setImageDrawable(requireContext().getDrawable(R.drawable.message_icon))
        binding.deliveryHeader.leftToolbarButton.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setUpAdapter() {
        packingItemsAdapter =
            PackingItemsAdapter(interaction = this, context = requireContext(), showArrow = false)
        val layoutManager = LinearLayoutManager(requireContext())
        binding.packingList.layoutManager = layoutManager
        binding.packingList.adapter = packingItemsAdapter
    }

    private fun observePackingItemsEvents() {
        viewModel.packingItemsFlow.onEach { state ->
            when (state) {
                PackingItemsViewState.Empty -> progressBarManager.dismiss()
                is PackingItemsViewState.Error -> {
                    progressBarManager.dismiss()
                    state.errorMessage?.let { showErrorBanner(message = state.errorMessage) }
                }

                is PackingItemsViewState.GetPackingItemsResult -> {
                    progressBarManager.dismiss()
                    if (state.packingItems?.items.isNullOrEmpty()) {
                        binding.emptyLayout.visibility = View.VISIBLE
                        binding.packingList.visibility = View.GONE

                    } else {
                        binding.emptyLayout.visibility = View.GONE
                        binding.packingList.visibility = View.VISIBLE
                    }
                    packingItemsAdapter.submitList(state.packingItems?.items?.filter { s -> s.packingListId == packingId })
                    packingSharedViewModel.packingItems =
                        state.packingItems?.items?.filter { s -> s.packingListId == packingId }
                }


                PackingItemsViewState.Loading -> progressBarManager.show()
                PackingItemsViewState.Reset -> progressBarManager.dismiss()
                is PackingItemsViewState.StartPackingResult -> {
                    progressBarManager.dismiss()
                    val action =
                        PackingItemsFragmentDirections.actionPackingItemsFragmentToFinalisePackingFragment()
                    packingId?.let { action.packingId = it }
                    findNavController().navigate(action)
                }

                is PackingItemsViewState.GetPackingListComment -> {
                    progressBarManager.dismiss()
                    comment = state.comment
                    lifecycleScope.launch {
                        val savedComment = dataStoreManager.getPackingListComment()
                        if (savedComment != comment && comment?.isNotBlank() == true) {
                            binding.deliveryHeader.iconBadge.visibility = View.VISIBLE
                            openCommentBottomSheet()
                        } else {
                            binding.deliveryHeader.iconBadge.visibility = View.GONE
                        }
                    }

                }
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun showBannerAccordingToStatus() {
        if (packingSharedViewModel.selectedPackingListItem?.status == PackingItemStatus.CLS.name
            || packingSharedViewModel.selectedPackingListItem?.status == PackingItemStatus.CNC.name
            || packingSharedViewModel.selectedPackingListItem?.status == PackingItemStatus.CNR.name
            || packingSharedViewModel.selectedPackingListItem?.status == PackingItemStatus.DLV.name
        ) {

            val status =
                PackingItemStatus.valueOf(packingSharedViewModel.selectedPackingListItem?.status!!)
            showInformationBanner(
                message = "Packing list : ${status.type}",
            )
        }
    }

    override fun onViewClicked(data: PackingItem) {

    }

    private fun openCommentBottomSheet() {
        binding.deliveryHeader.iconBadge.visibility = View.GONE
        lifecycleScope.launch {
            dataStoreManager.savePackingListComment(comment ?: "")
        }
        val bottomSheet = PackingListCommentBottomSheet.newInstance(
            descriptionText = comment ?: ""
        )
        bottomSheet.show(parentFragmentManager, bottomSheet.tag)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        viewModel.onEvent(PackingItemsEvent.Empty)
    }


}