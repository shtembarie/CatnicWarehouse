package com.example.catnicwarehouse.base

import androidx.fragment.app.Fragment
import com.example.catnicwarehouse.R
import com.example.shared.utils.BannerBar
import com.example.shared.utils.ProgressBarManager

abstract class BaseFragment : Fragment() {


    val progressBarManager by lazy { ProgressBarManager(requireActivity()) }

    fun showErrorBanner(message: String, displayDuration: Long = 5000) {

        BannerBar.build(requireActivity())
            .setTitle(message)
            .setLayoutGravity(BannerBar.TOP)
            .setBackgroundColor(R.color.red)
            .setDuration(displayDuration)
            .setSwipeToDismiss(true)
            .show()
    }

    fun showPositiveBanner(message: String, displayDuration: Long = 3000) {
        BannerBar.build(requireActivity())
            .setTitle(message)
            .setLayoutGravity(BannerBar.TOP)
            .setBackgroundColor(R.color.positive_progress_color)
            .setDuration(displayDuration)
            .setSwipeToDismiss(true)
            .show()
    }

    fun showInformationBanner(
        message: String,
        color: Int = R.color.orange_color,
        displayDuration: Long = 3000
    ) {
        BannerBar.build(requireActivity())
            .setTitle(message)
            .setLayoutGravity(BannerBar.TOP)
            .setBackgroundColor(color)
            .setDuration(displayDuration)
            .setSwipeToDismiss(true)
            .show()
    }

}