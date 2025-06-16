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
import com.example.catnicwarehouse.databinding.FragmentSearchDialogBinding
import com.example.catnicwarehouse.incoming.suppliers.presentation.adapter.SearchedCustomerResultAdapter
import com.example.catnicwarehouse.incoming.suppliers.presentation.sealedClasses.SearchCustomerEvent
import com.example.catnicwarehouse.incoming.suppliers.presentation.sealedClasses.SearchCustomerViewState
import com.example.catnicwarehouse.incoming.suppliers.presentation.viewModel.SearchCustomerDialogViewModel
import com.example.catnicwarehouse.shared.presentation.model.VendorOrCustomerInfo
import com.example.catnicwarehouse.shared.presentation.sealedClasses.SharedEvent
import com.example.catnicwarehouse.shared.presentation.viewModel.SharedViewModelNew
import com.example.shared.networking.network.customer.model.SearchedCustomerDTOItem
import com.example.shared.utils.BannerBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchCustomersDialogFragment : DialogFragment() {

    // Using ViewBinding to bind the layout
    private var _binding: FragmentSearchDialogBinding? = null
    private val binding get() = _binding!!
    private var itemList: List<SearchedCustomerDTOItem> = listOf()

    private lateinit var adapter: SearchedCustomerResultAdapter
    private val viewModel: SearchCustomerDialogViewModel by viewModels()
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
        updateUIBasedOnEnteredText(customers = emptyList())
        binding.icClearSearch.setOnClickListener {
            dialog?.dismiss()
        }
    }

    private fun setupRecyclerView() {
        adapter = SearchedCustomerResultAdapter(itemList) { selectedCustomerDTO ->

            sharedViewModel.onEvents(
                SharedEvent.UpdateSupplierInfo(
                    VendorOrCustomerInfo(
                        vendorId = null,
                        name = selectedCustomerDTO.company1 ?: "",
                        customerId = selectedCustomerDTO.customerId
                    )
                )
            )

            parentFragmentManager.setFragmentResult(
                "handleSuccessCustomerSelection",
                bundleOf(
                    "customerId" to selectedCustomerDTO.customerId
                )
            )
            dialog?.dismiss()
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
                    loadCustomers(s.toString())
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun updateUIBasedOnEnteredText(customers: List<SearchedCustomerDTOItem>?) {
        with(binding) {
            if (!customers.isNullOrEmpty()) {
                separatorView.visibility = View.VISIBLE
                rView.visibility = View.VISIBLE
            } else {
                separatorView.visibility = View.GONE
                rView.visibility = View.GONE
            }
        }
    }

    private fun loadCustomers(searchTerm: String? = null) {
        if (searchTerm.isNullOrEmpty())
            viewModel.onEvent(SearchCustomerEvent.EmptySearch)
        else
            viewModel.onEvent(SearchCustomerEvent.SearchCustomer(searchTerm))
    }

    private fun observeSearchedVendorData() {
        viewModel.searchedCustomerFlow.onEach { state ->
            when (state) {
                SearchCustomerViewState.Empty -> {
                    adapter.updateList(emptyList())
                    updateUIBasedOnEnteredText(emptyList())
                }

                is SearchCustomerViewState.Error -> {
                    state.errorMessage?.let { showErrorBanner(message = state.errorMessage) }
                }

                SearchCustomerViewState.Loading -> {
                }

                SearchCustomerViewState.Reset -> {}
                is SearchCustomerViewState.SearchedCustomers -> {
                    if (binding.searchEditText.text.toString().isEmpty().not()) {
                        state.customers?.let { adapter.updateList(it) }
                        updateUIBasedOnEnteredText(state.customers)
                    }
                }

                is SearchCustomerViewState.DeliveryCreated -> {}
                is SearchCustomerViewState.ArticlesForDeliveryFound -> {}
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
        fun newInstance(): SearchCustomersDialogFragment = SearchCustomersDialogFragment()
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
