package com.example.catnicwarehouse.scan.presentation.helper

import com.example.shared.networking.network.Stockyards.WarehouseStockyardsDTO

interface OnItemSelectListener {
        fun onItemSelected(item: WarehouseStockyardsDTO)
    }