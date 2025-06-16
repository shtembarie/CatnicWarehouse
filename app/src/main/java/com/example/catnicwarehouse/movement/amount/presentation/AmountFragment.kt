package com.example.catnicwarehouse.movement.amount.presentation

import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.text.SpannableString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.base.BaseFragment
import com.example.catnicwarehouse.databinding.FragmentAmountBinding
import com.example.catnicwarehouse.movement.shared.MovementActionType
import com.example.catnicwarehouse.movement.shared.MovementsSharedViewModel
import com.example.catnicwarehouse.utils.colorSubstringFromCharacter


class AmountFragment : BaseFragment() {

    private var _binding: FragmentAmountBinding? = null
    private val binding get() = _binding!!
    private val movementSharedViewModel: MovementsSharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAmountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handelHeaderSection()
        populateUIWithData()
        handleSaveButtonButton()
    }

    private fun handleSaveButtonButton() {
        binding.saveAmountButton.setOnClickListener {

            val updatedAmount = binding.amountItemText.text.toString().trim()

            if (movementSharedViewModel.movementActionType == MovementActionType.PICK_UP) {
                movementSharedViewModel.selectedArticle?.let { selectedArticle ->
                    selectedArticle.amountTakenForPickUp =
                        if (updatedAmount.isEmpty()) 0f else updatedAmount.toFloatOrNull() ?: 0f
                    findNavController().navigateUp()
                    return@setOnClickListener
                }
            } else if (movementSharedViewModel.movementActionType == MovementActionType.DROP_OFF) {
                movementSharedViewModel.currentMovementItemToDropOff?.let { selectedMovementItem ->
                    selectedMovementItem.amountTakenForDropOff =
                        if (updatedAmount.isEmpty()) 0f else updatedAmount.toFloatOrNull() ?: 0f
                    findNavController().navigateUp()
                    return@setOnClickListener
                }
            }

        }


        binding.saveAmountText.setOnClickListener {
            val updatedAmount = binding.amountItemText.text.toString().trim()

            if (movementSharedViewModel.movementActionType == MovementActionType.PICK_UP) {
                movementSharedViewModel.selectedArticle?.let { selectedArticle ->
                    selectedArticle.amountTakenForPickUp =
                        if (updatedAmount.isEmpty()) 0f else updatedAmount.toFloatOrNull() ?: 0f
                    findNavController().navigateUp()
                    return@setOnClickListener
                }
            } else if (movementSharedViewModel.movementActionType == MovementActionType.DROP_OFF) {
                movementSharedViewModel.currentMovementItemToDropOff?.let { selectedMovementItem ->
                    selectedMovementItem.amountTakenForDropOff =
                        if (updatedAmount.isEmpty()) 0f else updatedAmount.toFloatOrNull() ?: 0f
                    findNavController().navigateUp()
                    return@setOnClickListener
                }
            }
        }
    }

    private fun populateUIWithData() {
        binding.amountItemText.inputType = InputType.TYPE_CLASS_NUMBER
        binding.amountItemText.imeOptions = EditorInfo.IME_ACTION_DONE
        binding.articleCodeTextInput.isEndIconVisible = false
        binding.changeUnitTypeLayout.isVisible = false

        var valueWithUnit: SpannableString? = null
        var value: String? = null
        if (movementSharedViewModel.movementActionType == MovementActionType.PICK_UP) {
            val pickUPAmount =
                "${(movementSharedViewModel.selectedArticle?.amountTakenForPickUp ?: 0.0f).toInt()}/${movementSharedViewModel.selectedArticle?.unitCode ?: ""}"
            val pickUpAmountSpannableString =
                pickUPAmount.colorSubstringFromCharacter('/', Color.LTGRAY)

            value = "${
                (movementSharedViewModel.selectedArticle?.amountTakenForPickUp ?: 0.0f).toInt()
            }"
            valueWithUnit = pickUpAmountSpannableString

        } else {
            val dropOffAmount =
                "${
                    ((movementSharedViewModel.currentMovementItemToDropOff?.amountTakenForDropOff)?.toInt() ?: 0.0f).toInt()
                }/${movementSharedViewModel.currentMovementItemToDropOff?.unitCode ?: ""}"
            val dropOffAmountSpannableString =
                dropOffAmount.colorSubstringFromCharacter('/', Color.LTGRAY)
            valueWithUnit = dropOffAmountSpannableString
            value = "${
                ((movementSharedViewModel.currentMovementItemToDropOff?.amountTakenForDropOff)?.toInt() ?: 0.0f).toInt()
            }"
        }
        binding.amountItemText.setText(valueWithUnit)
        // Add focus change listener
        binding.amountItemText.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                // Clear the text when EditText is focused
                if (value.toInt() == 0) {
                    binding.amountItemText.setText("")
                } else
                    binding.amountItemText.setText("${(value).toInt()}")

            } else {
                // Restore the text when focus is lost
                binding.amountItemText.setText(valueWithUnit)

            }
        }
    }

    private fun handelHeaderSection() {
        binding.deliveryHeader.headerTitle.text = getString(R.string.amount_of_items)
        binding.deliveryHeader.toolbarSection.visibility = View.VISIBLE
        binding.deliveryHeader.rightToolbarButton.visibility = View.VISIBLE
        binding.deliveryHeader.rightToolbarButton.visibility = View.GONE
        binding.deliveryHeader.leftToolbarButton.setOnClickListener {
            findNavController().navigateUp()
        }
    }
}