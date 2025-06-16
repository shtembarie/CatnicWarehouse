package com.example.catnicwarehouse.Inventory.scanStockyardInventory.presentation

import android.graphics.Color
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.catnicwarehouse.Inventory.scanStockyardInventory.presentation.viewModel.MatchFoundInventoryViewModel
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.base.BaseFragment
import com.example.catnicwarehouse.databinding.FragmentMatchFoundInventoryBinding
import com.example.catnicwarehouse.incoming.defective.presentation.bottomSheetFragment.DefectiveArticleBottomSheetFragment
import com.example.catnicwarehouse.incoming.matchFound.presentation.sealedClass.MatchFoundViewState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


@AndroidEntryPoint
class MatchFoundInventoryFragment : BaseFragment() {

    private var _binding: FragmentMatchFoundInventoryBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MatchFoundInventoryViewModel by viewModels()
    private var deliveryItemId: String = ""
    private var deliveryId: String = ""



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMatchFoundInventoryBinding.inflate(inflater,container,false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        handelHeaderSection()
        observeMatchFoundEvents()

    }

    private fun observeMatchFoundEvents() {
        viewModel.matchFoundFlow.onEach { state ->
            when (state) {
                is MatchFoundViewState.DeliveryItemCreated -> {
                    progressBarManager.dismiss()
                    deliveryId = state.deliveryId
                    deliveryItemId = state.deliveryItemId.toString()
                    showPositiveBanner(getString(R.string.delivery_item_created))
                }

                MatchFoundViewState.Empty -> progressBarManager.dismiss()
                is MatchFoundViewState.Error -> {
                    progressBarManager.dismiss()
                    state.errorMessage?.let { showErrorBanner(message = state.errorMessage) }
                }

                MatchFoundViewState.Loading -> progressBarManager.show()
                MatchFoundViewState.Reset -> progressBarManager.dismiss()
                is MatchFoundViewState.DeliveryItemBooked -> {
                    progressBarManager.dismiss()
                    findNavController().navigate(R.id.action_matchFoundFragment_to_articlesFragment)
                }

                is MatchFoundViewState.DeliveryItemUpdated -> {
                    progressBarManager.dismiss()
                    if (state.isItemUpdated == true)
                        showPositiveBanner(getString(R.string.delivery_item_udpated))
                }
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }
    private fun showDefectiveArticleBottomSheet() {
        val bottomSheet = DefectiveArticleBottomSheetFragment()
        bottomSheet.show(childFragmentManager, bottomSheet.tag)
    }
    private fun handleBackPress() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().popBackStack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
        handleBackButton()
    }
    private fun handleBackButton() {
        binding.matchFoundHeader.leftToolbarButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }
    private fun handelHeaderSection(){
        binding.matchFoundHeader.headerTitle.text = getString(R.string.header_match_found)
        binding.matchFoundHeader.toolbarSection.visibility = View.VISIBLE
        binding.matchFoundHeader.leftToolbarButton.setOnClickListener {
            requireActivity().finish()
        }

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }





}