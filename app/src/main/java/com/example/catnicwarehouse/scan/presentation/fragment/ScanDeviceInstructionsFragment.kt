package com.example.catnicwarehouse.scan.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.catnicwarehouse.base.BaseFragment
import com.example.catnicwarehouse.databinding.ScanDeviceInfoLayoutBinding
import com.example.catnicwarehouse.scan.presentation.enums.ScanOptionEnum
import com.example.catnicwarehouse.scan.presentation.enums.ScanType
import com.example.shared.local.dataStore.DataStoreManager

import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ScanDeviceInstructionsFragment : BaseFragment() {

    private var _binding: ScanDeviceInfoLayoutBinding? = null
    private val binding get() = _binding!!

    private val args: ScanDeviceInstructionsFragmentArgs by navArgs()
    private var scanType: ScanType = ScanType.STOCKYARD
    @Inject
    lateinit var dataStoreManager: DataStoreManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = ScanDeviceInfoLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        args.scanType.let {
            scanType = ScanType.valueOf(it)
        }

        binding.buttonContinue.setOnClickListener{
//            lifecycleScope.launch {
//                dataStoreManager.setFirstScan(isFirstScan = false)
//                parentFragmentManager.setFragmentResult(
//                    "scanOptionBottomSheet",
//                    bundleOf("scanType" to scanType, "scanOption" to ScanOptionEnum.BARCODE)
//                )
                findNavController().popBackStack()
//            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



}