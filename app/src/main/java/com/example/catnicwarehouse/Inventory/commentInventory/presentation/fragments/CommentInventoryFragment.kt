package com.example.catnicwarehouse.Inventory.commentInventory.presentation.fragments

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.catnicwarehouse.Inventory.commentInventory.presentation.viewModel.CommentInventoryViewModel
import com.example.catnicwarehouse.R

class CommentInventoryFragment : Fragment() {

    companion object {
        fun newInstance() = CommentInventoryFragment()
    }

    private lateinit var viewModel: CommentInventoryViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_comment_inventory, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(CommentInventoryViewModel::class.java)
        // TODO: Use the ViewModel
    }

}