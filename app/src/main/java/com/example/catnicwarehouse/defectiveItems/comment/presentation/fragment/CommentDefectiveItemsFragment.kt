package com.example.catnicwarehouse.defectiveItems.comment.presentation.fragment

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.base.BaseFragment
import com.example.catnicwarehouse.databinding.FragmentCommentDefectiveItemsBinding
import com.example.catnicwarehouse.defectiveItems.shared.viewModel.DefectiveArticleSharedViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CommentDefectiveItemsFragment : BaseFragment() {
    private var _binding: FragmentCommentDefectiveItemsBinding? = null
    private val binding get() = _binding!!
    private val defectiveArticleSharedViewModel: DefectiveArticleSharedViewModel by activityViewModels()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCommentDefectiveItemsBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleBackButton()
        getParams()
        setUpSaveButton()
    }
    private fun handleBackButton(){
        binding.commentHeader.headerTitle.text = getString(R.string.note)
        binding.commentHeader.leftToolbarButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }
    private fun getParams() {
        val initialAmount = defectiveArticleSharedViewModel.selectedDefectiveArticleUIModel?.comment ?: defectiveArticleSharedViewModel.selectedWarehouseStockyardInventoryEntry?.comment
        val updatedAmount = defectiveArticleSharedViewModel.updatedComment

        val amountText = updatedAmount ?: initialAmount ?:""
        binding.commentText.text = Editable.Factory.getInstance().newEditable(amountText)

    }
    private fun setUpSaveButton(){
        updateSaveButtonState(binding.commentText.text?.isNotEmpty() ?: false)
        binding.commentText.addTextChangedListener {
            updateSaveButtonState(it?.toString()?.isNotEmpty() ?: false)
        }

        binding.saveButton.setOnClickListener {
            saveItemAmount()
        }
    }
    private fun updateSaveButtonState(isEnabled: Boolean) {
        val drawableRes = if (isEnabled) {
            R.drawable.orange_rounded_button
        } else {
            R.drawable.grey_rounded_button
        }
        binding.saveButton.setBackgroundResource(drawableRes)
    }
    private fun saveItemAmount() {
        val itemCommentText = binding.commentText.text.toString()
        val commentAmount = itemCommentText.toString()
        if (commentAmount != null) {
            defectiveArticleSharedViewModel.saveUpdatedComment(commentAmount)
        }
        findNavController().popBackStack()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}