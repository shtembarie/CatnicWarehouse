package com.example.catnicwarehouse.movement.movementList.presentation.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.base.BaseFragment
import com.example.catnicwarehouse.databinding.FragmentMovementSelectionBinding
import com.example.catnicwarehouse.databinding.FragmentMovementsListBinding
import com.example.catnicwarehouse.movement.movementList.presentation.activity.MovementsActivity
import com.example.catnicwarehouse.movement.movementList.presentation.adapter.MovementsAdapter
import com.example.catnicwarehouse.movement.movementList.presentation.adapter.MovementsAdapterInteraction
import com.example.catnicwarehouse.movement.shared.MovementActionType
import com.example.catnicwarehouse.movement.shared.MovementsSharedViewModel
import com.example.shared.repository.movements.MovementItem
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MovementSelectionFragment : BaseFragment(), MovementsAdapterInteraction {

    private var _binding: FragmentMovementSelectionBinding? = null
    private val binding get() = _binding!!

    private lateinit var movementsAdapter: MovementsAdapter
    private val movementsSharedViewModel: MovementsSharedViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMovementSelectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handelHeaderSection()
        setUpAdapter()
        movementsAdapter.submitList(movementsSharedViewModel.movementItemsListToSelectFrom?.filter { s->s.movementOpen })
    }

    private fun handelHeaderSection() {
        binding.deliveryHeader.headerTitle.text = getString(R.string.movements_list)
        binding.deliveryHeader.toolbarSection.visibility = View.VISIBLE
        binding.deliveryHeader.rightToolbarButton.visibility = View.VISIBLE
        binding.deliveryHeader.rightToolbarButton.visibility = View.GONE
        binding.deliveryHeader.leftToolbarButton.setOnClickListener {
            (requireActivity() as MovementsActivity).finish()
        }
    }

    private fun setUpAdapter() {
        movementsAdapter = MovementsAdapter(interaction = this,showArrow = true)
        val layoutManager = LinearLayoutManager(requireContext())
        binding.movementsList.layoutManager = layoutManager
        binding.movementsList.adapter = movementsAdapter
    }

    override fun onViewClicked(data: MovementItem) {
        movementsSharedViewModel.currentMovementItemToDropOff = data
        movementsSharedViewModel.scannedArticle = null
        if (movementsSharedViewModel.movementActionType == MovementActionType.DROP_OFF)
            movementsSharedViewModel.selectedArticle = null
        val action =
            MovementSelectionFragmentDirections.actionMovementSelectionFragmentToMatchFoundFragment3()
        findNavController().navigate(action)
    }


}