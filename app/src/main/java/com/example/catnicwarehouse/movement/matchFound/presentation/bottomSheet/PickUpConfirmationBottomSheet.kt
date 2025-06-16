package com.example.catnicwarehouse.movement.matchFound.presentation.bottomSheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.catnicwarehouse.databinding.AddArticleToMovementsBottomSheetBinding
import com.example.catnicwarehouse.movement.shared.MovementsSharedViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PickUpConfirmationBottomSheet(private val itemClickListener: () -> Unit): BottomSheetDialogFragment(){

    private var _binding: AddArticleToMovementsBottomSheetBinding? = null
    private val binding get() = _binding!!

    private val movementSharedViewModel: MovementsSharedViewModel by activityViewModels()



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AddArticleToMovementsBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        populateUIWithData()
        binding.buttonContinue.setOnClickListener {
            itemClickListener()
            dialog?.dismiss()
        }

        binding.buttonBack.setOnClickListener {
            dialog?.dismiss()
        }
    }


    private fun populateUIWithData(){
        with(binding){
            articleNoTextView.text = movementSharedViewModel.selectedArticle?.articleId?:""
            amountTextView.text = (movementSharedViewModel.selectedArticle?.amountTakenForPickUp?:0.0f).toInt().toString()
            amountUnitCodeTextView.text = "/ ${movementSharedViewModel.selectedArticle?.unitCode}"
        }
    }



    override fun onStart() {
        super.onStart()
        val bottomSheet =
            dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        bottomSheet?.let {
            val behavior = BottomSheetBehavior.from(it)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
//            it.layoutParams.height = (resources.displayMetrics.heightPixels * 0.92).toInt()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }




}
