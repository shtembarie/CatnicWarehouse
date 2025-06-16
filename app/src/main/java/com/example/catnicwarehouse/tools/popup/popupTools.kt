package com.example.catnicwarehouse.tools.popup

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.databinding.DialogDeliveryCompletedBinding
import com.example.catnicwarehouse.databinding.DialogErrorBinding
import com.example.catnicwarehouse.databinding.DialogErrorPurchaseConfirmBinding
import com.example.catnicwarehouse.databinding.DialogErrorScanPopUpBinding
import com.example.catnicwarehouse.databinding.DialogSuccessScanPopupBinding
import com.example.catnicwarehouse.databinding.InventoryErrorBinding
import kotlinx.coroutines.*

@SuppressLint("MissingInflatedId")
fun showErrorScanPopup(activity: Activity, callback: () -> Unit = {}) {
    val binding = DialogErrorScanPopUpBinding.inflate(activity.layoutInflater)
    val dialog = androidx.appcompat.app.AlertDialog.Builder(activity)
        .setView(binding.root)
        .setCancelable(false)
        .create()

    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

    binding.scanBtn.setOnClickListener {
        dialog.dismiss()
        callback()
    }
    dialog.show()
}

fun showInventoryErrorDialog(activity: Activity, callback: () -> Unit) {
    val binding = InventoryErrorBinding.inflate(activity.layoutInflater)
    val dialog = AlertDialog.Builder(activity)
        .setView(binding.root)
        .setCancelable(false)
        .create()

    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    binding.newIncomingButton.setOnClickListener {
        dialog.dismiss()
        callback()
    }

    dialog.show()
}

fun showErrorPurchaseOrderConfirmation(activity: Activity, callback: () -> Unit = {}) {
    val binding = DialogErrorPurchaseConfirmBinding.inflate(activity.layoutInflater)
    val dialog = androidx.appcompat.app.AlertDialog.Builder(activity)
        .setView(binding.root)
        .setCancelable(false)
        .create()

    binding.button.setOnClickListener {
        dialog.dismiss()
        callback()
    }
    dialog.show()
}

fun showDeliveryCompleteDialog(
    activity: Activity,
    positiveClick: () -> Unit = {},
    neutralClick: () -> Unit = {},
) {
    val binding = DialogDeliveryCompletedBinding.inflate(activity.layoutInflater)
    val dialog = androidx.appcompat.app.AlertDialog.Builder(activity)
        .setView(binding.root)
        .setCancelable(false)
        .create()

    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

    binding.newIncomingButton.setOnClickListener {
        dialog.dismiss()
        positiveClick()
    }
    binding.backButton.setOnClickListener {
        dialog.dismiss()
        neutralClick()
    }
    dialog.show()
}


fun showExitDialog(
    activity: Activity,
    showIcon: Boolean = true,
    title: String = activity.getString(R.string.exit),
    description: String = activity.getString(R.string.leave_desc),
    positiveButtonText: String = activity.getString(R.string.exit),
    positiveClick: () -> Unit = {},
    neutralClick: () -> Unit = {},
) {
    val binding = DialogErrorBinding.inflate(activity.layoutInflater)
    val dialog = androidx.appcompat.app.AlertDialog.Builder(activity)
        .setView(binding.root)
        .setCancelable(false)
        .create()

    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

    binding.titlePopup.text = title
    binding.descriptionPopup.text = description

    if (showIcon)
        binding.barcodeImage.visibility = View.VISIBLE
    else
        binding.barcodeImage.visibility = View.GONE

    binding.newIncomingButton.text = positiveButtonText

    binding.newIncomingButton.setOnClickListener {
        dialog.dismiss()
        positiveClick()
    }
    binding.backButton.setOnClickListener {
        dialog.dismiss()
        neutralClick()
    }
    dialog.show()
}


fun showSuccessScanPopup(
    activity: Activity,
    titleText: String,
    descriptionText: String,
    button1Text: String,
    button2Text: String,
    button1Callback: () -> Unit = {},
    button2Callback: () -> Unit = {}
) {
    val binding = DialogSuccessScanPopupBinding.inflate(activity.layoutInflater)
    val dialog = androidx.appcompat.app.AlertDialog.Builder(activity)
        .setView(binding.root)
        .setCancelable(false)
        .create()

    binding.scannedNumber.text = titleText
    binding.descriptionPopup.text = descriptionText
    binding.scanBtn.text = button1Text
    binding.scanAgainBtn.text = button2Text
    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

    binding.scanBtn.setOnClickListener {
        dialog.dismiss()
        button1Callback()
    }

    binding.scanAgainBtn.setOnClickListener {
        dialog.dismiss()
        button2Callback()
    }

    dialog.show()
}

fun showCloseMovementConfirmationPopup(
    activity: Activity,
    titleText: String,
    descriptionText: String,
    button1Text: String,
    button1Callback: () -> Unit = {},
) {
    val binding = DialogSuccessScanPopupBinding.inflate(activity.layoutInflater)
    val dialog = androidx.appcompat.app.AlertDialog.Builder(activity)
        .setView(binding.root)
        .setCancelable(false)
        .create()

    binding.scannedNumber.text = titleText
    binding.barcodeImage.setImageDrawable(activity.getDrawable(R.drawable.close_movement_confirmation))
    binding.descriptionPopup.text = descriptionText
    binding.scanBtn.text = button1Text
    binding.scannedNumber.textSize = 18f
    binding.scanAgainBtn.visibility = View.GONE
    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

    binding.scanBtn.setOnClickListener {
        dialog.dismiss()
        button1Callback()
    }



    dialog.show()
}

