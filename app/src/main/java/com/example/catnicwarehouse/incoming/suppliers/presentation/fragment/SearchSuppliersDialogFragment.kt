package com.example.catnicwarehouse.incoming.suppliers.presentation.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.incoming.suppliers.presentation.adapter.SearchResultsAdapter
import com.example.catnicwarehouse.databinding.FragmentSearchDialogBinding
import com.example.catnicwarehouse.shared.presentation.model.VendorOrCustomerInfo
import com.example.catnicwarehouse.incoming.suppliers.presentation.sealedClasses.SearchVendorEvent
import com.example.catnicwarehouse.incoming.suppliers.presentation.sealedClasses.SearchVendorViewState
import com.example.catnicwarehouse.incoming.suppliers.presentation.viewModel.SearchDialogViewModel
import com.example.catnicwarehouse.shared.presentation.sealedClasses.SharedEvent
import com.example.catnicwarehouse.shared.presentation.viewModel.SharedViewModelNew
import com.example.shared.networking.network.supplier.model.SearchedVendorDTO
import com.example.shared.utils.BannerBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchSuppliersDialogFragment : DialogFragment() {

    // Using ViewBinding to bind the layout
    private var _binding: FragmentSearchDialogBinding? = null
    private val binding get() = _binding!!
    private var itemList: List<SearchedVendorDTO> = listOf()

    private lateinit var adapter: SearchResultsAdapter
    private val viewModel: SearchDialogViewModel by viewModels()
    private val sharedViewModel: SharedViewModelNew by activityViewModels()

    private var searchJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSearchDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        setupRecyclerView()
        setupSearch()
        observeSearchedVendorData()
    }

    private fun init() {
        updateUIBasedOnEnteredText(vendors = emptyList())
        binding.icClearSearch.setOnClickListener {
            dialog?.dismiss()
        }
    }

    private fun setupRecyclerView() {
        adapter = SearchResultsAdapter(itemList) { selectedVendorDTO ->
            dialog?.dismiss()
            sharedViewModel.onEvents(
                SharedEvent.UpdateSupplierInfo(
                    VendorOrCustomerInfo(
                        vendorId = selectedVendorDTO.vendorId ,
                        name = selectedVendorDTO.company1 ?: "",
                        customerId = null
                    )
                )
            )
            parentFragmentManager.setFragmentResult(
                "handleSuccessVendorSelection",
                bundleOf(
                    "vendorId" to selectedVendorDTO.vendorId
                )
            )
        }
        val layoutManager = LinearLayoutManager(requireContext())
        binding.rView.layoutManager = layoutManager
        binding.rView.adapter = adapter
    }

    private fun setupSearch() {
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

                searchJob?.cancel()
                searchJob = lifecycleScope.launch {
                    delay(400)
                    loadVendors(s.toString())
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun updateUIBasedOnEnteredText(vendors: List<SearchedVendorDTO>?) {
        with(binding) {
            if (!vendors.isNullOrEmpty()) {
                separatorView.visibility = View.VISIBLE
                rView.visibility = View.VISIBLE
            } else {
                separatorView.visibility = View.GONE
                rView.visibility = View.GONE
            }
        }
    }

    private fun loadVendors(searchTerm: String? = null) {
        if (searchTerm.isNullOrEmpty())
            viewModel.onEvent(SearchVendorEvent.EmptySearch)
        else
            viewModel.onEvent(SearchVendorEvent.SearchVendor(searchTerm))
    }

    private fun observeSearchedVendorData() {
        viewModel.searchedVendorsFlow.onEach { state ->
            when (state) {
                SearchVendorViewState.Empty -> {
                    adapter.updateList(emptyList())
                    updateUIBasedOnEnteredText(emptyList())
                }

                is SearchVendorViewState.Error -> {
                    state.errorMessage?.let { showErrorBanner(message = state.errorMessage) }
                }

                SearchVendorViewState.Loading -> {
                }

                SearchVendorViewState.Reset -> {}
                is SearchVendorViewState.SearchedVendors -> {
                    if (binding.searchEditText.text.toString().isEmpty().not()) {
                        state.vendors?.let { adapter.updateList(it) }
                        updateUIBasedOnEnteredText(state.vendors)
                    }
                }

                is SearchVendorViewState.DeliveryCreated -> {}
                is SearchVendorViewState.ArticlesForDeliveryFound -> {}
            }

        }.launchIn(viewLifecycleOwner.lifecycleScope)
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

    companion object {
        fun newInstance(): SearchSuppliersDialogFragment = SearchSuppliersDialogFragment()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setBackgroundDrawableResource(R.drawable.rounded_edit_text_background)

            val metrics = resources.displayMetrics
            val width = metrics.widthPixels - (20 * metrics.density).toInt()
            val topMargin = (10 * metrics.density).toInt()

            setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
            val attributes = attributes
            attributes?.gravity = Gravity.TOP
            attributes?.y = topMargin
            this.attributes = attributes

        }

    }
}
