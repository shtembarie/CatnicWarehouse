package com.example.catnicwarehouse.Inventory.stockyardMatchFound.presentation.fragment

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.catnicwarehouse.Inventory.stockyardMatchFound.presentation.sealedClasses.MatchFoundInventoryViewState
import com.example.catnicwarehouse.Inventory.stockyardMatchFound.presentation.viewModel.MatchFoundInventoryViewModel
import com.example.catnicwarehouse.Inventory.stockyards.presentation.sealedClasses.GetInventoryByIdViewState
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.base.BaseFragment
import com.example.catnicwarehouse.databinding.FragmentInventoryArticleDescriptionBinding
import com.example.catnicwarehouse.sharedinventory.presentation.InventorySharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class MatchFoundInventoryItemFragment : BaseFragment() {
    private var _binding: FragmentInventoryArticleDescriptionBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MatchFoundInventoryViewModel by viewModels()
    private val inventorySharedViewModel: InventorySharedViewModel by activityViewModels()
    var unitCode:String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentInventoryArticleDescriptionBinding.inflate(inflater,container,false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val articleId = arguments?.getString("articleId")
        val articleDescription = arguments?.getString("articleDescription")
        val amount = arguments?.getInt("amount")
        val comment = arguments?.getString("comment")
        val matchCode = arguments?.getString("matchCode")
        unitCode = arguments?.getString("unitCode")



        binding.idSection.titleId.findViewById<TextView>(R.id.title_id).text = matchCode
        binding.articleDescription.titleId.findViewById<TextView>(R.id.title_id).text = articleDescription
        binding.articleSection.titleId.findViewById<TextView>(R.id.title_id).text = articleId
        binding.quantitySection.titleId.findViewById<TextView>(R.id.title_id).text = amount?.toString() ?: ""
        binding.commentSection.titleId.findViewById<TextView>(R.id.title_id).text = comment ?: ""


        handleHeaderSection()
        observeMatchInventory()
        displayItemsDescription()
        setupClickListeners()
    }
    private fun setupClickListeners() {
        binding.commentSection.viewContainer.setOnClickListener {
            findNavController().navigate(R.id.action_inventoryArticleDescriptionFragment_to_commentFragmentInventors)
        }
        binding.quantitySection.viewContainer.setOnClickListener {
            findNavController().navigate(R.id.action_inventoryArticleDescriptionFragment_to_amountItemInventoryFragment)
        }
        binding.articleDescription.viewContainer.setOnClickListener {
            showFullArticleDescription()
        }

    }
    private fun displayItemsDescription() {
        val redColor = ContextCompat.getColor(requireContext(), R.color.light_green)
        val codeMatchText = getString(R.string.code_match_found)
        val spannableString = SpannableString(codeMatchText).apply {
            setSpan(ForegroundColorSpan(redColor), 0, codeMatchText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        binding.idSection.decsriptionId.text = spannableString

        val articleMatchCode = inventorySharedViewModel.articleMatchCode
        binding.idSection.titleId.findViewById<TextView>(R.id.title_id).text = articleMatchCode

        val itemId = inventorySharedViewModel.itemId

        if(itemId!=null)
            inventorySharedViewModel.saveUpdatedActualUnitCode(itemId = itemId,
                unitCode = unitCode
            )

        val articleDescription = inventorySharedViewModel.articlesDescription
        binding.articleDescription.titleId.findViewById<TextView>(R.id.title_id).text = articleDescription
        binding.articleDescription.decsriptionId.text = getString(R.string.article_description)
        val articleDescriptionIcon = binding.articleDescription.imgProduct.findViewById<ImageView>(R.id.img_product)
        articleDescriptionIcon.setImageResource(R.drawable.article_description)

        val savedComment = itemId?.let { inventorySharedViewModel.updatedItemComment[it] } ?: inventorySharedViewModel.clickedArticleComment
        binding.commentSection.titleId.findViewById<TextView>(R.id.title_id).text = if (savedComment.isNullOrEmpty() || savedComment == "string") "" else savedComment
        binding.commentSection.decsriptionId.text = getString(R.string.comment_this_article)

        val articleId = inventorySharedViewModel.articleId
        binding.articleSection.titleId.findViewById<TextView>(R.id.title_id).text = articleId.toString()
        binding.articleSection.decsriptionId.text = getString(R.string.article_number)

        val actualStock = itemId?.let { inventorySharedViewModel.updatedItemAmount[it].takeIf { it != -1 } }
            ?: inventorySharedViewModel.clickedItemIdActualStock
        binding.quantitySection.titleId.findViewById<TextView>(R.id.title_id).text = if (actualStock == -1) "" else actualStock.toString()


        if (itemId != null) {



            if (actualStock != null) {
                inventorySharedViewModel.saveUpdatedItemAmount(itemId, actualStock)
            }
        }

        binding.quantitySection.decsriptionId.text = getString(R.string.counted_in_stock)

        val countedInStock = binding.quantitySection.imgProduct.findViewById<ImageView>(R.id.img_product)
        countedInStock.setImageResource(R.drawable.countedinstock)

        val leaveYourNote = binding.commentSection.imgProduct.findViewById<ImageView>(R.id.img_product)
        leaveYourNote.setImageResource(R.drawable.leaveyournote)

        binding.idSection.toRightBtn.visibility = View.GONE
        binding.articleSection.toRightBtn.visibility = View.GONE

    }
    private fun observeMatchInventory(){
        viewModel.matchFoundFlow.onEach { state ->
            when(state) {
                is GetInventoryByIdViewState.InventoryItemUpdated ->{
                    progressBarManager.dismiss()
                    findNavController().popBackStack()
                }
                GetInventoryByIdViewState.Empty ->
                    progressBarManager.dismiss()
                is GetInventoryByIdViewState.Error -> {
                    progressBarManager.dismiss()
                    state.errorMessage?.let { showErrorBanner(message = state.errorMessage) }
                }
                GetInventoryByIdViewState.Loading -> progressBarManager.show()
                GetInventoryByIdViewState.Reset -> progressBarManager.dismiss()
                else -> {}
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)
            binding.confirmButton.setOnClickListener {
                findNavController().popBackStack()
            }
    }
    private fun handleHeaderSection(){
        val inventoryId = arguments?.getString("articleId") ?: -1 //arguments?.getInt("inventoryArticle") ?: -1
        val headerText = "$inventoryId"
        binding.articleHeader.headerTitle.text = headerText
        binding.articleHeader.toolbarSection.visibility = View.VISIBLE
        binding.articleHeader.leftToolbarButton.setOnClickListener {
            showExitConfirmationDialog()
        }
    }
    private fun showExitConfirmationDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_error, null)
        val alertDialog = AlertDialog.Builder(requireContext()).setView(dialogView).create()

        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogView.findViewById<TextView>(R.id.backButton).setOnClickListener {
            alertDialog.dismiss()
        }
        dialogView.findViewById<TextView>(R.id.newIncomingButton).setOnClickListener {
            val clickedItemId = inventorySharedViewModel.itemId

            inventorySharedViewModel.clickedArticleComment?.let {
                if (clickedItemId != null) {
                    inventorySharedViewModel.saveUpdatedComment(clickedItemId, it)
                }
            }

            inventorySharedViewModel.clickedItemIdActualStock?.let {
                if (clickedItemId != null) {
                    inventorySharedViewModel.saveUpdatedItemAmount(clickedItemId, it)
                }
            }
            alertDialog.dismiss()
            findNavController().popBackStack()
        }
        alertDialog.show()
    }
    private fun showFullArticleDescription() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.article_description, null)
        val alertDialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val fullDescriptionTextView = dialogView.findViewById<TextView>(R.id.full_description_text)
        val fullTitleTextView = dialogView.findViewById<TextView>(R.id.full_title_text)
        val fullDescription = inventorySharedViewModel.articlesDescription
        val fullTitle = inventorySharedViewModel.articleMatchCode
        fullTitleTextView.text = fullTitle
        fullDescriptionTextView.text = fullDescription

        dialogView.findViewById<TextView>(R.id.backButton).setOnClickListener {
            alertDialog.dismiss()
        }
        dialogView.findViewById<TextView>(R.id.newIncomingButton).setOnClickListener {
            val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val combinedText = "$fullTitle\n\n$fullDescription"
            val clip = ClipData.newPlainText("Article Text", combinedText)
            clipboard.setPrimaryClip(clip)
            alertDialog.dismiss()
        }
        alertDialog.show()
    }
    override fun onResume() {
        super.onResume()
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            showExitConfirmationDialog()
        }
    }

}