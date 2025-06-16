package com.example.catnicwarehouse.packing.finalisePackingList.presentation.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.fragment.app.activityViewModels
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.base.BaseFragment
import com.example.catnicwarehouse.databinding.FragmentDetailsBinding
import com.example.catnicwarehouse.databinding.FragmentFinaliseParentBinding
import com.example.catnicwarehouse.packing.shared.presentation.viewModel.PackingSharedViewModel
import com.example.catnicwarehouse.tools.ext.toFormattedDate
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_details.packingInformationHeader

@AndroidEntryPoint
class DetailsFragment : BaseFragment() {

    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!

    private val packingSharedViewModel: PackingSharedViewModel by this.activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateData()
    }

    private fun updateData(){
        binding.detailRow1.icon.setImageDrawable(getDrawable(requireContext(),R.drawable.briefcase_icon))
        binding.detailRow1.subtitleLeft.text = getString(R.string.customer_name)
        binding.detailRow1.subtitleRight.text = getString(R.string.city)
        binding.detailRow1.titleLeft.text = packingSharedViewModel.selectedPackingListItem?.customerAddressCompany1
        binding.detailRow1.titleRight.text = packingSharedViewModel.selectedPackingListItem?.customerAddressCity



        binding.detailRow2.icon.setImageDrawable(getDrawable(requireContext(),R.drawable.hash_img))
        binding.detailRow2.subtitleLeft.text = getString(R.string.customer_no)
        binding.detailRow2.subtitleRight.text = getString(R.string.order_no)
        binding.detailRow2.titleLeft.text = packingSharedViewModel.selectedPackingListItem?.customerId
        binding.detailRow2.titleRight.text = packingSharedViewModel.selectedPackingListItem?.orderId


        binding.detailRow3.icon.setImageDrawable(getDrawable(requireContext(),R.drawable.calendar_icon))
        binding.detailRow3.subtitleLeft.text = getString(R.string.document_date)
        binding.detailRow3.subtitleRight.text = getString(R.string.delivery_date)
        binding.detailRow3.titleRight.text = packingSharedViewModel.selectedPackingListItem?.packingListDate?.toFormattedDate()
        binding.detailRow3.titleLeft.text = packingSharedViewModel.selectedPackingListItem?.deliveryDate?.toFormattedDate()


        binding.detailRow4.icon.setImageDrawable(getDrawable(requireContext(),R.drawable.headphone_icon))
        binding.detailRow4.subtitleLeft.text = getString(R.string.clerk)
        binding.detailRow4.titleLeft.text = packingSharedViewModel.selectedPackingListItem?.packingListCreatedBy
        binding.detailRow4.rightLayout.visibility = View.GONE

        binding.detailRow5.icon.setImageDrawable(getDrawable(requireContext(),R.drawable.weighing_scale_icon))
        binding.detailRow5.subtitleLeft.text = getString(R.string.total_weight)
        binding.detailRow5.titleLeft.text = packingSharedViewModel.selectedPackingListItem?.totalWeight.toString()
        binding.detailRow5.rightLayout.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}