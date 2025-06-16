package com.example.catnicwarehouse.defectiveItems.stockyards.presentation.bottomSheet

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.catnicwarehouse.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

/**
 * Created by Enoklit on 04.12.2024.
 */
object BottomSheetHelper {
    fun showBottomSheetDialog(
        context: Context,
        onNewIncomingClick: () -> Unit,
        onBackClick: () -> Unit
    ) {
        val dialogView = LayoutInflater.from(context).inflate(
            R.layout.close_list_from_defective_articles,
            null
        )
        val bottomSheetDialog = BottomSheetDialog(context)

        bottomSheetDialog.setContentView(dialogView)

        dialogView.findViewById<TextView>(R.id.newIncomingButton).setOnClickListener {
            onNewIncomingClick()
            bottomSheetDialog.dismiss()
        }
        dialogView.findViewById<TextView>(R.id.backButton).setOnClickListener {
            onBackClick()
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.setOnShowListener { dialogInterface ->
            val bottomSheet = (dialogInterface as BottomSheetDialog)
                .findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let {
                val layoutParams = it.layoutParams
                layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                it.layoutParams = layoutParams

                val behavior = BottomSheetBehavior.from(it)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
        bottomSheetDialog.show()
    }
}
