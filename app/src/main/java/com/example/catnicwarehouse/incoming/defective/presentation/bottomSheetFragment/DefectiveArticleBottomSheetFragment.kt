package com.example.catnicwarehouse.incoming.defective.presentation.bottomSheetFragment

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.databinding.DefectiveArticleBottomSheetFragmentBinding
import com.example.catnicwarehouse.incoming.matchFound.presentation.sealedClass.DefectiveReason
import com.example.catnicwarehouse.shared.presentation.enums.CommentType
import com.example.catnicwarehouse.shared.presentation.enums.ItemType
import com.example.catnicwarehouse.shared.presentation.viewModel.SharedViewModelNew
import com.example.shared.utils.ProgressBarManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DefectiveArticleBottomSheetFragment : BottomSheetDialogFragment(),
    DefectiveReasonsBottomSheetFragment.DefectiveReasonListener {

    private var _binding: DefectiveArticleBottomSheetFragmentBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: SharedViewModelNew by activityViewModels()
    private val progressBarManager by lazy { ProgressBarManager(requireActivity()) }

    private var defectiveAmount: String? = null
    private var defectiveAmountUnit: String? = null
    private var defectiveComment: String? = null
    private var defectiveReason: DefectiveReason? = null

    interface DefectiveValuesUpdateListener {
        fun onDefectiveValuesUpdated(
            defectiveAmount: String?,
            defectiveAmountUnit: String?,
            defectiveComment: String?,
            defectiveReason: DefectiveReason?
        )
    }

    var listener: DefectiveValuesUpdateListener? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DefectiveArticleBottomSheetFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleClickActions()
        init(
            localDefectiveQty = defectiveAmount ?: sharedViewModel.getSelectedDefectiveQty() ?: "0",
            localDefectiveComment = defectiveComment
                ?: sharedViewModel.getSelectedDefectiveComment(),
            localDefectiveReason = defectiveReason ?: sharedViewModel.getSelectedDefectiveReason()
            ?: DefectiveReason.PHYSICAL_DAMAGE,
        )
    }

    private fun handleClickActions() {
        fun handleMarkAsDefectiveButtonAction() {

            binding.buttonContinue.setOnClickListener {
                listener?.onDefectiveValuesUpdated(defectiveAmount=defectiveAmount,defectiveAmountUnit=defectiveAmountUnit,defectiveComment=defectiveComment, defectiveReason = defectiveReason)
                dismiss()
            }
        }

        fun handleCancelButtonAction() {
            binding.buttonBack.setOnClickListener {
                dismiss()
            }
        }

        fun handleDefectiveAmountItemsButtonAction() {
            binding.defectiveAmountSection.viewContainer.setOnClickListener {
                val bundle = Bundle().apply {
                    putString(
                        "amount",
                        defectiveAmount ?: sharedViewModel.getSelectedDefectiveQty() ?: "0"
                    )
                    putString(
                        "unit",
                        defectiveAmountUnit ?: sharedViewModel.getSelectedDefectiveUnit() ?: ""
                    )
                    putString("itemType", ItemType.DEFECTIVE.name)
                }
                findNavController().navigate(R.id.amountItemFragment, bundle)
            }
        }

        fun handleDefectiveReasonButtonAction() {
            binding.defectiveReasonSection.viewContainer.setOnClickListener {
                showDefectiveReasonBottomSheet()
            }
        }

        fun handleDefectiveCommentButtonAction() {
            binding.defectiveCommentSection.viewContainer.setOnClickListener {
                navigateToCommentFragment()
            }
        }

        handleMarkAsDefectiveButtonAction()
        handleCancelButtonAction()
        handleDefectiveAmountItemsButtonAction()
        handleDefectiveReasonButtonAction()
        handleDefectiveCommentButtonAction()

    }

    fun handleReceivedResultFromDefectiveQtyAndUnit(
        amount: String,
        amountUnit: String,
        itemType: String
    ) {
        defectiveAmount = amount
        defectiveAmountUnit = amountUnit

        init(
            localDefectiveQty = defectiveAmount ?: sharedViewModel.getSelectedDefectiveQty()
            ?: "0",
            localDefectiveComment = defectiveComment
                ?: sharedViewModel.getSelectedDefectiveComment(),
            localDefectiveReason = defectiveReason
                ?: sharedViewModel.getSelectedDefectiveReason()
                ?: DefectiveReason.PHYSICAL_DAMAGE,
        )


    }


    fun handleReceivedResultFromDefectiveComment(
        comment: String,
        commentType: String,
    ) {
        defectiveComment = comment


        init(
            localDefectiveQty = defectiveAmount ?: sharedViewModel.getSelectedDefectiveQty()
            ?: "0",
            localDefectiveComment = defectiveComment
                ?: sharedViewModel.getSelectedDefectiveComment(),
            localDefectiveReason = defectiveReason
                ?: sharedViewModel.getSelectedDefectiveReason()
                ?: DefectiveReason.PHYSICAL_DAMAGE,
        )


    }


    private fun init(
        localDefectiveQty: String?,
        localDefectiveReason: DefectiveReason,
        localDefectiveComment: String?
    ) {
        with(binding) {
            textViewTitle.text = getString(R.string.defective_articles)
            textViewSubtitle.text = getString(R.string.defective_article_subtitle)

            //amount of defective items
            with(defectiveAmountSection) {
                imgProduct.setImageDrawable(
                    AppCompatResources.getDrawable(
                        requireContext(),
                        R.drawable._defective_item_with_grey_circular_background_img
                    )
                )
                titleId.text = localDefectiveQty
                decsriptionId.text = getString(R.string.amount_of_defective_items)
            }

            //defective reason
            with(defectiveReasonSection) {
                imgProduct.setImageDrawable(
                    AppCompatResources.getDrawable(
                        requireContext(),
                        R.drawable.defective_reason_with_grey_circular_background_img
                    )
                )
                titleId.text = localDefectiveReason.value
                decsriptionId.text = getString(R.string.defective_reason)
            }

            //defective comment
            with(defectiveCommentSection) {
                imgProduct.setImageDrawable(
                    AppCompatResources.getDrawable(
                        requireContext(),
                        R.drawable.defective_comment_with_grey_circular_background_img
                    )
                )
                titleId.maxLines = 1
                titleId.ellipsize = TextUtils.TruncateAt.END
                titleId.text = localDefectiveComment
                titleId.hint = getString(R.string.leave_your_note)
                decsriptionId.text = getString(R.string.comments)
            }

            //confirm button
            buttonContinue.text = getString(R.string.yes_mark_as_defective)
        }
    }

    private fun showDefectiveReasonBottomSheet() {
        val bottomSheet = DefectiveReasonsBottomSheetFragment.newInstance(
            defectiveReason ?: sharedViewModel.getSelectedDefectiveReason()
            ?: DefectiveReason.PHYSICAL_DAMAGE
        ).apply {
            listener = this@DefectiveArticleBottomSheetFragment
        }
        bottomSheet.show(childFragmentManager, bottomSheet.tag)
    }

    private fun navigateToCommentFragment() {
        val commentType = CommentType.DEFECTIVE
        val bundle = bundleOf(
            "commentType" to commentType.name,
            "comment" to defectiveComment
        )
        findNavController().navigate(R.id.commentFragment, bundle)
    }

    override fun onStart() {
        super.onStart()
        val bottomSheet =
            dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        bottomSheet?.let {
            val behavior = BottomSheetBehavior.from(it)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            it.layoutParams.height = (resources.displayMetrics.heightPixels * 0.92).toInt()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDefectiveReasonSelected(defectiveReason: DefectiveReason) {
        this.defectiveReason = defectiveReason
        init(
            localDefectiveQty = defectiveAmount ?: sharedViewModel.getSelectedDefectiveQty() ?: "0",
            localDefectiveComment = defectiveComment
                ?: sharedViewModel.getSelectedDefectiveComment(),
            localDefectiveReason = defectiveReason,
        )
    }


}
