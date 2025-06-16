package com.example.shared.utils

import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentActivity
import com.example.data.R
import com.example.data.databinding.ProgressBarBinding

class ProgressBarManager(val activity: FragmentActivity) {

    private var binding: ProgressBarBinding? = null
    private var dialog: AlertDialog? = null

    init {
        dialog = createProgressDialog(activity)
        dialog?.setCancelable(false)
        dialog?.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    private fun createProgressDialog(context: Activity): AlertDialog {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = ProgressBarBinding.inflate(inflater)
        binding?.pbar?.indeterminateTintList = ColorStateList.valueOf(context.getColor(R.color.blood_orange))

        val builder = AlertDialog.Builder(context).setView(binding?.root).setCancelable(false)
        return builder.create()
    }

    fun show() {
        if (dialog != null && dialog?.isShowing == true) dialog?.dismiss()
        if (dialog?.isShowing == false && !activity.isDestroyed && !activity.isFinishing)
            dialog?.show()
    }

    fun dismiss() {
        dialog?.dismiss()
    }
}