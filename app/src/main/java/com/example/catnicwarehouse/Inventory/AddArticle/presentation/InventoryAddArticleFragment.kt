package com.example.catnicwarehouse.Inventory.AddArticle.presentation

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.catnicwarehouse.Inventory.AddArticle.presentation.viewModel.InventoryAddArticleViewModel
import com.example.catnicwarehouse.R

class InventoryAddArticleFragment : Fragment() {

    companion object {
        fun newInstance() = InventoryAddArticleFragment()
    }

    private lateinit var viewModel: InventoryAddArticleViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_inventory_add_article, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(InventoryAddArticleViewModel::class.java)
        // TODO: Use the ViewModel
    }

}