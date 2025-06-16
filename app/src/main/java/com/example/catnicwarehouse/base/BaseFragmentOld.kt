package com.example.catnicwarehouse.base

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.createViewModelLazy
import com.example.catnicwarehouse.R
import com.example.shared.utils.BannerBar
import com.example.shared.utils.ProgressBarManager
import com.google.android.material.snackbar.Snackbar
import timber.log.Timber
import java.lang.reflect.ParameterizedType

abstract class BaseFragmentOld<VM : BaseViewModel> : Fragment() {

    protected lateinit var viewModel: VM

    private val type = (javaClass.genericSuperclass as ParameterizedType)
    private val classVM = type.actualTypeArguments[0] as Class<VM>


    var progressBar: View? = null

    val progressBarManager by lazy { ProgressBarManager(requireActivity()) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = createViewModelLazy(classVM.kotlin, { viewModelStore }).value // - Experimental
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeToSnackbarMessages()
        subscribeToMainProgressBar()
        setProgressBar()
    }

    private fun showSnackbarMessage(message: String) {
        view?.let { Snackbar.make(it, message, Snackbar.LENGTH_LONG).show() }
    }

    fun showSnackbarMessage(messageResource: Int) {
        view?.let { Snackbar.make(it, getString(messageResource), Snackbar.LENGTH_LONG).show() }
    }

    abstract fun setProgressBar()

    private fun subscribeToMainProgressBar() {
        viewModel.mainProgressBar.observe(viewLifecycleOwner) { state ->
            progressBar?.apply {
                visibility = if (state) View.VISIBLE else View.GONE
            } ?: kotlin.run {
                Timber.d("Progress bar not found")
                assert(progressBar == null) { Timber.d("Progress bar not found") }
            }
        }
    }

    private fun subscribeToSnackbarMessages() {
        viewModel.snackbarMessages.observe(viewLifecycleOwner) { message ->
            showSnackbarMessage(message)
        }
    }



}