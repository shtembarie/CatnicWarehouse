package com.example.catnicwarehouse.packing.packingList.presentation.fragment

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.databinding.FragmentSearchPackingListDialogBinding
import com.example.catnicwarehouse.incoming.suppliers.presentation.sealedClasses.SearchVendorViewState
import com.example.catnicwarehouse.packing.packingList.presentation.adapter.SearchPackingListAdapter
import com.example.catnicwarehouse.packing.packingList.presentation.sealedClasses.SearchPackingListEvent
import com.example.catnicwarehouse.packing.packingList.presentation.sealedClasses.SearchPackingListViewState
import com.example.catnicwarehouse.packing.packingList.presentation.utils.PackingListSelectionListener
import com.example.catnicwarehouse.packing.packingList.presentation.viewModel.SearchDialogViewModel
import com.example.shared.networking.network.packing.model.packingList.SearchPackingListDTO
import com.example.shared.utils.BannerBar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchPackingListDialogFragment : DialogFragment() {

    private var _binding: FragmentSearchPackingListDialogBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: SearchPackingListAdapter
    private val viewModel: SearchDialogViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchPackingListDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    private var listener: PackingListSelectionListener? = null

    companion object {
        fun newInstance(packingListSelectionListener: PackingListSelectionListener): SearchPackingListDialogFragment {
            val fragment = SearchPackingListDialogFragment()
            fragment.listener = packingListSelectionListener // Safely assign the listener here
            return fragment
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null // Avoid memory leaks by nullifying the reference
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupSearch()
        observePackingListData()
        init()
    }

    private fun init() {
        binding.icClearSearch.setOnClickListener {
            dialog?.dismiss()
        }
    }


    private fun setupRecyclerView() {
        adapter = SearchPackingListAdapter(emptyList()) { selectedItem ->
            listener?.onPackingListSelected(selectedItem)
            dialog?.dismiss()
        }
        binding.rView.layoutManager = LinearLayoutManager(requireContext())
        binding.rView.adapter = adapter
    }

    private fun setupSearch() {
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString()
                if (query.length >= 3) {
                    viewModel.onEvent(SearchPackingListEvent.SearchPackingList(query))
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun observePackingListData() {
        lifecycleScope.launchWhenStarted {
            viewModel.searchedPackingListsFlow.collect { state ->
                when (state) {
                    is SearchPackingListViewState.Empty -> {
                        adapter.updateList(emptyList())
                        updateUI(emptyList())
                    }

                    is SearchPackingListViewState.SearchedPackingLists -> {
                        state.packingLists?.let {
                            adapter.updateList(it)
                            updateUI(it)
                        }

                    }

                    is SearchPackingListViewState.Error -> {
                        state.errorMessage?.let { showErrorBanner(message = state.errorMessage) }
                    }


                    SearchPackingListViewState.Reset -> {}
                }
            }
        }
    }

    private fun showErrorBanner(message: String, displayDuration: Long = 3000) {

        BannerBar.build(requireActivity())
            .setTitle(message)
            .setLayoutGravity(BannerBar.TOP)
            .setBackgroundColor(R.color.red)
            .setDuration(displayDuration)
            .setSwipeToDismiss(true)
            .show()
    }

    private fun updateUI(items: List<SearchPackingListDTO>) {
        binding.separatorView.visibility = if (items.isNotEmpty()) View.VISIBLE else View.GONE
        binding.rView.visibility = if (items.isNotEmpty()) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
