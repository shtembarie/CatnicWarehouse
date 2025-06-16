package com.example.catnicwarehouse.movement.summary.presentation.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.base.BaseFragment
import com.example.catnicwarehouse.databinding.FragmentMovementSummaryBinding
import com.example.catnicwarehouse.movement.shared.MovementsSharedViewModel
import com.example.catnicwarehouse.movement.summary.presentation.sealedClasses.MovementSummaryEvent
import com.example.catnicwarehouse.movement.summary.presentation.sealedClasses.MovementSummaryViewState
import com.example.catnicwarehouse.movement.summary.presentation.viewModel.MovementSummaryViewModel
import com.example.catnicwarehouse.utils.colorSubstringFromCharacter
import com.example.shared.repository.movements.DropOffRequestModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class MovementSummaryFragment : BaseFragment() {

    private var _binding: FragmentMovementSummaryBinding? = null
    private val binding get() = _binding!!
    private val movementSharedViewModel: MovementsSharedViewModel by activityViewModels()
    private val viewModel: MovementSummaryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentMovementSummaryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleHeaderSection()
        handleConfirmButtonAction()
        populateUIWithData()
        observeMovementSummaryEvents()
    }

    private fun handleConfirmButtonAction() {
        binding.confirmButton.setOnClickListener {
            viewModel.onEvent(MovementSummaryEvent.DropOff(movementSharedViewModel.currentMovement?.id.toString(),
                DropOffRequestModel(
                    movementItemId = movementSharedViewModel.currentMovementItemToDropOff?.id,
                    unitCode = movementSharedViewModel.currentMovementItemToDropOff?.unitCode,
                    amount = movementSharedViewModel.currentMovementItemToDropOff?.amountTakenForDropOff?.toInt(),
                    destinationWarehouseStockYardId = movementSharedViewModel.currentMovementItemToDropOff?.destinationWarehouseStockYardId
                )
            ))
        }
    }

    private fun handleHeaderSection() {
        binding.deliveryHeader.headerTitle.text = getString(R.string.movement_summary)
        binding.deliveryHeader.toolbarSection.visibility = View.VISIBLE
        binding.deliveryHeader.title.text = getString(R.string.you_want_to_confirm_this_item_movement_as_completed)
        binding.deliveryHeader.leftToolbarButton.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun populateUIWithData(){
        with(binding){

            articleToMoveTitle.text = getString(R.string.article_to_move)
            //Source
            sourceTitle.text = getString(R.string.from)
            sourceSubtitle.text = (movementSharedViewModel.currentMovementItemToDropOff?.sourceWarehouseStockYardName?:"").toString()

            val articleAmount = "${movementSharedViewModel.currentMovementItemToDropOff?.amount}/${movementSharedViewModel.currentMovementItemToDropOff?.unitCode}"
            val articleAmountSpannableString =
                articleAmount.colorSubstringFromCharacter('/', Color.LTGRAY)
            sourceUnit.text = articleAmountSpannableString

            //Destination
            destinationTitle.text =getString(R.string.to)
            destinationSubtitle.text = (movementSharedViewModel.currentMovementItemToDropOff?.destinationWarehouseStockYardName?:"").toString()
            val articleAmountDropped = "${(movementSharedViewModel.currentMovementItemToDropOff?.amountTakenForDropOff)?.toInt()}/${movementSharedViewModel.currentMovementItemToDropOff?.unitCode}"
            val articleAmountDroppedSpannableString =
                articleAmountDropped.colorSubstringFromCharacter('/', Color.LTGRAY)
            destinationUnit.text = articleAmountDroppedSpannableString

            //Article
            articleId.text = movementSharedViewModel.currentMovementItemToDropOff?.articleId
            subtitle1.text = movementSharedViewModel.currentMovementItemToDropOff?.sourceWarehouseStockYardName.toString()
            subtitle2.visibility =View.GONE
            unit.text = articleAmountSpannableString
        }
    }

    private fun observeMovementSummaryEvents(){
        viewModel.movementSummaryFlow.onEach {state->
         when(state){
             is MovementSummaryViewState.DropOffResult -> {
                 progressBarManager.dismiss()
                 movementSharedViewModel.itemsDropped = (movementSharedViewModel.currentMovementItemToDropOff?.amountTakenForDropOff?:0).toInt()
                 movementSharedViewModel.reset()
                 findNavController().popBackStack(destinationId = R.id.movementsListFragment, inclusive = false)
             }
             MovementSummaryViewState.Empty -> progressBarManager.dismiss()
             is MovementSummaryViewState.Error -> {
                 progressBarManager.dismiss()
                 state.errorMessage?.let { showErrorBanner(message = state.errorMessage) }
             }
             MovementSummaryViewState.Loading -> progressBarManager.show()
             MovementSummaryViewState.Reset -> progressBarManager.dismiss()
         }
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }




    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}