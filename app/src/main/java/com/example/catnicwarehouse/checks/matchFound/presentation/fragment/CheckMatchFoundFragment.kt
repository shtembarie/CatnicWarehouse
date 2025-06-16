package com.example.catnicwarehouse.checks.matchFound.presentation.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.checks.shared.presentation.viewModel.ChecksSharedViewModel
import com.example.catnicwarehouse.databinding.FragmentCheckMatchFoundBinding
import com.example.catnicwarehouse.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CheckMatchFoundFragment : Fragment() {

    private var _binding: FragmentCheckMatchFoundBinding? = null
    private val binding get() = _binding!!

    private val checksSharedViewModel: ChecksSharedViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCheckMatchFoundBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handelHeaderSection()
        populateDataOnUI()
    }

    private fun handelHeaderSection() {
        binding.deliveryHeader.headerTitle.text =
            checksSharedViewModel.selectedArticleToShowMatchFoundDetails?.articleMatchCode
        binding.deliveryHeader.toolbarSection.visibility = View.VISIBLE
        binding.deliveryHeader.rightToolbarButton.visibility = View.VISIBLE
        binding.deliveryHeader.rightToolbarButton.visibility = View.GONE
        binding.deliveryHeader.leftToolbarButton.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun populateDataOnUI() {
        with(binding) {
            //Article Number
            idSection.titleId.text =
                checksSharedViewModel.selectedArticleToShowMatchFoundDetails?.articleId
            idSection.decsriptionId.text = getString(R.string.article_number)
            idSection.toRightBtn.visibility = View.GONE
            idSection.imgProduct.setImageDrawable(
                AppCompatResources.getDrawable(
                    requireContext(),
                    R.drawable.hash_img
                )
            )

            //Location
            articleSection.titleId.text =
                checksSharedViewModel.selectedArticleToShowMatchFoundDetails?.stockYardName
            articleSection.decsriptionId.text = getString(R.string.location)
            articleSection.toRightBtn.visibility = View.GONE
            articleSection.imgProduct.setImageDrawable(
                AppCompatResources.getDrawable(
                    requireContext(),
                    R.drawable.marker_leading_icon
                )
            )

            //Amount
            quantitySection.titleId.text =
                checksSharedViewModel.selectedArticleToShowMatchFoundDetails?.amount.toString()
            quantitySection.decsriptionId.text = getString(R.string.amount_of_items)
            quantitySection.toRightBtn.visibility = View.GONE
            quantitySection.imgProduct.setImageDrawable(
                AppCompatResources.getDrawable(
                    requireContext(),
                    R.drawable.amount_leading_icon
                )
            )

            //comment
            if(checksSharedViewModel.selectedArticleToShowMatchFoundDetails?.comment.isNullOrEmpty())
                defectiveSection.viewContainer.visibility = View.GONE
            else
                defectiveSection.viewContainer.visibility = View.VISIBLE

            defectiveSection.titleId.text =
                checksSharedViewModel.selectedArticleToShowMatchFoundDetails?.comment
                    ?: getString(R.string.none)
            defectiveSection.decsriptionId.text = getString(R.string.comment)

            defectiveSection.toRightBtn.visibility = View.GONE
            defectiveSection.imgProduct.setImageDrawable(
                AppCompatResources.getDrawable(
                    requireContext(),
                    R.drawable.defective_comment_with_grey_circular_background_img
                )
            )
        }
    }

}