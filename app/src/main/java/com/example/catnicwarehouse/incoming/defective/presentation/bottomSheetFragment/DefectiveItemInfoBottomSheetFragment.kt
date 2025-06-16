package com.example.catnicwarehouse.incoming.defective.presentation.bottomSheetFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.databinding.DefectiveInfoFragmentBinding
import com.example.shared.utils.ProgressBarManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DefectiveItemInfoBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: DefectiveInfoFragmentBinding? = null
    private val binding get() = _binding!!
    private val progressBarManager by lazy { ProgressBarManager(requireActivity()) }



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DefectiveInfoFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }



    private fun init(){
        with(binding){
            textViewTitle.text = getString(R.string.what_is_a_defective_item)
            defectiveDesc.text = getString(R.string.defective_info_desc)
            buttonBack.setOnClickListener{
                dismiss()
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
