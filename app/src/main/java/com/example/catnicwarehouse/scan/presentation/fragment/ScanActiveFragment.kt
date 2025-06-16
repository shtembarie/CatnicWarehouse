package com.example.catnicwarehouse.scan.presentation.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.databinding.FragmentScanActiveBinding
import com.example.catnicwarehouse.scan.presentation.helper.ScanEventListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ScanActiveFragment : DialogFragment() {

    private var _binding: FragmentScanActiveBinding? = null
    private val binding get() = _binding!!

    var onManualInputClick: (() -> Unit)? = null
    var onCancelClick: (() -> Unit)? = null
    var onActiveScanClick: ((MotionEvent) -> Unit)? = null

    var scanEventListener: ScanEventListener? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScanActiveBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.activeButton.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // Allow ripple effect to be handled by system
                    scanEventListener?.onScanActionDown()
                    onActiveScanClick?.invoke(event)
                    false // Let system handle visual feedback (ripple effect)
                }
                MotionEvent.ACTION_UP -> {

                    scanEventListener?.onScanActionUp()
                    onActiveScanClick?.invoke(event)
                    false // You handle the release
                }
                else -> false
            }
        }


        binding.buttonConfirm.setOnClickListener {
            // Trigger manual input action callback
            onManualInputClick?.invoke()
        }

        binding.cancelButton.setOnClickListener {
            // Trigger cancel action callback
            onCancelClick?.invoke()
            dismiss()
        }
    }

    fun dismissPopup() {
        dismiss() // Method to programmatically dismiss the popup
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
            val width = metrics.widthPixels - (50 * metrics.density).toInt()
            val topMargin = (10 * metrics.density).toInt()

            setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
            val attributes = attributes
            attributes?.gravity = Gravity.CENTER
            attributes?.y = topMargin
            this.attributes = attributes

        }

    }

    fun updateTitle(title: String) {
        binding.title.text = title
    }


}