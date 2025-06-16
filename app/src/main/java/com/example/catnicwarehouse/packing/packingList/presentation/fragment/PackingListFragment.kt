package com.example.catnicwarehouse.packing.packingList.presentation.fragment

import android.os.Bundle
import android.util.Log
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
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.base.BaseFragment
import com.example.catnicwarehouse.databinding.FragmentPackingListBinding
import com.example.catnicwarehouse.packing.cancelledPackingListBottomSheet.presentation.CancelledPackingListBottomSheet
import com.example.catnicwarehouse.packing.cancelledPackingListBottomSheet.presentation.DropZoneBottomSheet
import com.example.catnicwarehouse.packing.finalisePackingList.presentation.bottomSheet.CancelPackingListBottomSheet
import com.example.catnicwarehouse.packing.finalisePackingList.presentation.fragment.FinalisePackingFragmentArgs
import com.example.catnicwarehouse.packing.finalisePackingList.presentation.sealedClasses.FinalisePackingEvent
import com.example.catnicwarehouse.packing.packingItem.presentation.sealedClasses.PackingItemsEvent
import com.example.catnicwarehouse.packing.packingList.presentation.adapter.ConnectedPackingListsAdapter
import com.example.catnicwarehouse.packing.packingList.presentation.adapter.ConnectedPackingListsInteraction
import com.example.catnicwarehouse.packing.packingList.presentation.adapter.PackingListAdapter
import com.example.catnicwarehouse.packing.packingList.presentation.adapter.PackingListAdapterInteraction
import com.example.catnicwarehouse.packing.packingList.presentation.sealedClasses.PackingListEvent
import com.example.catnicwarehouse.packing.packingList.presentation.sealedClasses.PackingListViewState
import com.example.catnicwarehouse.packing.packingList.presentation.utils.PackingListSelectionListener
import com.example.catnicwarehouse.packing.packingList.presentation.viewModel.PackingListViewModel
import com.example.catnicwarehouse.packing.shared.PackingItemStatus
import com.example.catnicwarehouse.packing.shared.presentation.activity.PackingActivity
import com.example.catnicwarehouse.packing.shared.presentation.viewModel.PackingSharedViewModel
import com.example.shared.networking.network.packing.model.defaultPackingZone.DefaultPackingZoneResultModel
import com.example.shared.networking.network.packing.model.packingList.AssignedPackingListItem
import com.example.shared.networking.network.packing.model.packingList.ConnectedPackingList
import com.example.shared.networking.network.packing.model.packingList.SearchPackingListDTO
import com.example.shared.networking.network.packing.model.startPacking.CancelPackingRequestModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class PackingListFragment : BaseFragment(), PackingListAdapterInteraction,
    PackingListSelectionListener, ConnectedPackingListsInteraction,
    CancelledPackingListBottomSheet.CancelledPackingListListener {

    private var _binding: FragmentPackingListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PackingListViewModel by viewModels()
    private val packingSharedViewModel: PackingSharedViewModel by activityViewModels()
    private lateinit var packingListAdapter: PackingListAdapter
    private lateinit var connectedPackingListsAdapter: ConnectedPackingListsAdapter
    lateinit var cancelledPackingListBottomSheet: CancelledPackingListBottomSheet
    private var assignedPackingLists: ArrayList<AssignedPackingListItem>? = null

    private val args: PackingListFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (args.isForRelatedPackingList.not())
            packingSharedViewModel.initViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPackingListBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handelHeaderSection(getString(R.string.packing_list))
        setUpAdapter()
        observePackingListEvents()
        binding.emptyLayout.visibility = View.GONE
        binding.packingList.visibility = View.VISIBLE
        if (args.isForRelatedPackingList.not())
            viewModel.onEvent(PackingListEvent.GetAssignedPackingLists)
        else {
            setUpConnectedPackingListAdapter()
            connectedPackingListsAdapter.submitList(packingSharedViewModel.selectedPackingListItem?.connectedPackingLists)
        }
        handleBackPress()

    }

    private fun setUpConnectedPackingListAdapter() {
        connectedPackingListsAdapter = ConnectedPackingListsAdapter(this, requireContext())
        val layoutManager = LinearLayoutManager(requireContext())
        binding.packingList.layoutManager = layoutManager
        binding.packingList.adapter = connectedPackingListsAdapter
    }

    private fun observePackingListEvents() {
        viewModel.packingListFlow.onEach { state ->
            when (state) {
                PackingListViewState.Empty -> {
                    progressBarManager.dismiss()
                }

                is PackingListViewState.Error -> {
                    progressBarManager.dismiss()
                    state.errorMessage?.let { showErrorBanner(message = state.errorMessage) }
                }

                is PackingListViewState.GetPackingList -> {
                    progressBarManager.dismiss()

                    if (!args.isForRelatedPackingList)
                        packingSharedViewModel.selectedPackingListItem = state.packingList
                    else
                        packingSharedViewModel.selectedRelatedPackingListItem = state.packingList

                    if (state.packingList != null) {
                        packingSharedViewModel.selectedAssignedPackingListGroupItem = null
                        val action =
                            PackingListFragmentDirections.actionPackingListFragmentToPackingItemsFragment()
                        action.packingId = state.packingList.id
                        action.isForRelatedPackingList = args.isForRelatedPackingList
                        findNavController().navigate(action)
                    }
                }

                PackingListViewState.Loading -> progressBarManager.show()
                PackingListViewState.Reset -> progressBarManager.dismiss()
                is PackingListViewState.GetPackingLists -> {
                    progressBarManager.dismiss()

                }

                is PackingListViewState.GetAssignedPackingLists -> {
                    progressBarManager.dismiss()

                    if (state.packingList.isNullOrEmpty()) {
                        binding.emptyLayout.visibility = View.VISIBLE
                        binding.packingList.visibility = View.GONE
                    } else {
                        binding.emptyLayout.visibility = View.GONE
                        binding.packingList.visibility = View.VISIBLE
                    }


                    val sortedList = state.packingList
                        ?.sortedWith(
                            compareByDescending<AssignedPackingListItem> { it.packingListGroupPriority }
                                .thenBy { it.packingListPriority }
                        )
                        ?: emptyList()

                    // Separate items into ungrouped and grouped
                    val ungroupedItems = mutableListOf<AssignedPackingListItem>()
                    val groupedItemsMap = mutableMapOf<String, AssignedPackingListItem>()

                    for (item in sortedList) {
                        val code = item.packingListGroupCode
                        if (code.isNullOrEmpty() || code == "0") {
                            ungroupedItems.add(item)
                        } else {
                            if (!groupedItemsMap.containsKey(code)) {
                                groupedItemsMap[code] = item
                            }
                        }
                    }

                    val groupedItems = groupedItemsMap.values.toMutableList()
                    groupedItems.sortBy { it.packingListGroupName }

                    val finalList =  groupedItems + ungroupedItems

                    assignedPackingLists = ArrayList(sortedList)
                    packingListAdapter.submitList(finalList)
                    packingListAdapter.notifyDataSetChanged()
                }

                is PackingListViewState.GetDefaultPackingZonesResult -> {
                    progressBarManager.dismiss()
                    state.defaultPackingZones?.let {
                        openDefaultDropOffZoneBottomSheet(it)
                        cancelledPackingListBottomSheet.dismiss()
                    }
                }

                is PackingListViewState.GetCancelPackingListResult -> {
                    progressBarManager.dismiss()
                    if (state.isPackingListCancelled == true) {
                        viewModel.onEvent(PackingListEvent.GetAssignedPackingLists)
                    }
                }
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun setUpAdapter() {
        packingListAdapter = PackingListAdapter(
            interaction = this,
            context = requireContext(),
            viewModel = packingSharedViewModel
        )
        val layoutManager = LinearLayoutManager(requireContext())
        binding.packingList.layoutManager = layoutManager
        binding.packingList.adapter = packingListAdapter
    }

    private fun handelHeaderSection(headerString: String) {
        var header = headerString
        binding.deliveryHeader.rightToolbarButton.visibility = View.VISIBLE
        if (args.isForRelatedPackingList) {
            binding.deliveryHeader.rightToolbarButton.visibility = View.GONE
            header = getString(R.string.related_packing_list_2)
        }
        binding.deliveryHeader.headerTitle.text = header
        binding.deliveryHeader.toolbarSection.visibility = View.VISIBLE
        handleSearch()
    }

    private fun handleSearch() {
        binding.deliveryHeader.rightToolbarButton.setOnClickListener {
            showSearchDialog()
        }
    }

    private fun showSearchDialog() {
        val searchDialogFragment = SearchPackingListDialogFragment.newInstance(this)
        searchDialogFragment.isCancelable = false
        searchDialogFragment.show(parentFragmentManager, "searchDialog")
    }


    override fun onViewClicked(data: AssignedPackingListItem) {
        //For group packing lists
        if (data.packingListGroupCode.isNullOrEmpty()
                .not() && packingSharedViewModel.selectedAssignedPackingListGroupItem == null && assignedPackingLists != null
        ) {
            packingSharedViewModel.selectedAssignedPackingListGroupItem = data
            packingSharedViewModel.selectedAssignedPackingListGroupItem?.isInsideAGroup = true

            handelHeaderSection(getString(R.string.packing_lists, data.packingListGroupName))
            val filteredListForGroup =
                assignedPackingLists?.filter { s -> s.packingListGroupCode == data.packingListGroupCode }
            packingListAdapter.submitList(filteredListForGroup)
            packingListAdapter.notifyDataSetChanged()
        }//For All packing lists
        else {


            packingSharedViewModel.selectedAssignedPackingListItem = data
            packingSharedViewModel.selectedSearchedPackingListId = null

            if (data.appStatus == PackingItemStatus.CNC.name
                || data.appStatus == PackingItemStatus.CNR.name
            ) {
                openCancelledPackingListBottomSheet()
                return
            }
            viewModel.onEvent(PackingListEvent.GetPackingList(data.id))
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        viewModel.onEvent(PackingListEvent.Empty)
    }

    override fun onPackingListSelected(selectedPackingList: SearchPackingListDTO) {
        packingSharedViewModel.selectedSearchedPackingListId = selectedPackingList.packingListId
        packingSharedViewModel.selectedAssignedPackingListItem = null

        viewModel.onEvent(PackingListEvent.GetPackingList(selectedPackingList.packingListId))
    }

    override fun onConnectedPackingListClicked(data: ConnectedPackingList) {
        viewModel.onEvent(PackingListEvent.GetPackingList(data.id))
    }

    private fun handleBackPress() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                backLogic()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
        handleBackButton()
    }

    private fun handleBackButton() {
        binding.deliveryHeader.leftToolbarButton.setOnClickListener {
            backLogic()
        }
    }

    private fun backLogic() {
        if (packingSharedViewModel.selectedAssignedPackingListGroupItem?.isInsideAGroup == true) {
            handelHeaderSection(getString(R.string.packing_list))
            packingSharedViewModel.selectedAssignedPackingListGroupItem = null
            if (args.isForRelatedPackingList.not()) {
                viewModel.onEvent(PackingListEvent.GetAssignedPackingLists)
            } else {
                setUpConnectedPackingListAdapter()
                connectedPackingListsAdapter.submitList(packingSharedViewModel.selectedPackingListItem?.connectedPackingLists)
            }
        } else if (args.isForRelatedPackingList) {
            findNavController().popBackStack()
        }else {
            requireActivity().finish()
        }
    }

    private fun openCancelledPackingListBottomSheet() {
        cancelledPackingListBottomSheet = CancelledPackingListBottomSheet.newInstance(this)
        cancelledPackingListBottomSheet.show(
            parentFragmentManager,
            "CancelledPackingListBottomSheet"
        )
    }

    private fun openDefaultDropOffZoneBottomSheet(defaultPackingZones: List<DefaultPackingZoneResultModel>) {
        val bottomSheet = DropZoneBottomSheet(
            defaultPackingZones = defaultPackingZones,
        ) { selectedZone ->
            if (selectedZone != null) {
                viewModel.onEvent(
                    PackingListEvent.CancelPackingList(
                        (packingSharedViewModel.selectedAssignedPackingListItem?.id
                            ?: packingSharedViewModel.selectedSearchedPackingListId),
                        CancelPackingRequestModel(selectedZone.id ?: 0)
                    )
                )
            }
        }
        bottomSheet.show(parentFragmentManager, bottomSheet.tag)

    }

    override fun onResume() {
        super.onResume()
        // Disable the Activity's callback so this fragment's callback will be used
        (requireActivity() as PackingActivity).activityCallback.isEnabled = false
    }

    override fun onPause() {
        super.onPause()
        // Re-enable the Activity's callback if you want it active on other screens
        (requireActivity() as PackingActivity).activityCallback.isEnabled = true
    }

    override fun onChoosePackingZoneButtonFromCancelledPackingListClicked() {
        viewModel.onEvent(PackingListEvent.GetDefaultPackingZones)
    }


}