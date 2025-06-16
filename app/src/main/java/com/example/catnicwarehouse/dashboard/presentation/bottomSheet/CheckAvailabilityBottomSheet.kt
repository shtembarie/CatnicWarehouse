package com.example.catnicwarehouse.dashboard.presentation.bottomSheet

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.databinding.CheckAvailabilityBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class CheckAvailabilityBottomSheet(
    private val listener: CheckAvailabilityListener
) : BottomSheetDialogFragment() {

    companion object {
        fun newInstance(listener: CheckAvailabilityListener): CheckAvailabilityBottomSheet {
            return CheckAvailabilityBottomSheet(listener)
        }
    }

    private var _binding: CheckAvailabilityBottomSheetBinding? = null
    private val binding get() = _binding!!
    private var isArticleCheck:Boolean = true

    // Define the interface
    interface CheckAvailabilityListener {
        fun buttonClicked(isArticleCheck:Boolean)
        fun onBackClicked()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = CheckAvailabilityBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleButtonUI(buttonEnabled = false)
        handleScanOptionsSelectionUI()

        binding.buttonContinue.setOnClickListener {
            listener.buttonClicked(isArticleCheck = isArticleCheck)
        }

        // Back Button callback
        binding.buttonBack.setOnClickListener {
            listener.onBackClicked()
        }
    }

    private fun handleButtonUI(buttonEnabled: Boolean) {
        val context = requireContext()
        binding.buttonContinue.apply {
            val (backgroundRes, textColor) = if (!buttonEnabled) {
                R.drawable.grey_rounded_button to context.getColor(R.color.disabled_button_text_color)
            } else {
                R.drawable.orange_rounded_button to Color.WHITE
            }
            background = AppCompatResources.getDrawable(context, backgroundRes)
            setTextColor(textColor)
            isEnabled = buttonEnabled
        }

    }


    private fun handleScanOptionsSelectionUI() {
        binding.apply {
            val blackBorder =
                ContextCompat.getDrawable(requireContext(), R.drawable.black_outline_round_border)
            val greyBorder =
                ContextCompat.getDrawable(requireContext(), R.drawable.grey_outline_round_border)

            checkArticlesButton.setOnClickListener {
                arrayOf(
                    checkArticlesButton to blackBorder,
                    checkStockyardButton to greyBorder,
                ).forEach { (button, border) ->
                    button.background = border
                }
                arrayOf(
                    tickCheckArticlesIcon to true,
                    tickCheckStockyardsIcon to false,
                ).forEach { (icon, isVisible) ->
                    icon.isVisible = isVisible
                }
                isArticleCheck = true
                handleButtonUI(buttonEnabled = true)
            }
            checkStockyardButton.setOnClickListener {
                arrayOf(
                    checkArticlesButton to greyBorder,
                    checkStockyardButton to blackBorder,
                ).forEach { (button, border) ->
                    button.background = border
                }
                arrayOf(
                    tickCheckArticlesIcon to false,
                    tickCheckStockyardsIcon to true,
                ).forEach { (icon, isVisible) ->
                    icon.isVisible = isVisible
                }
                isArticleCheck = false
                handleButtonUI(buttonEnabled = true)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
