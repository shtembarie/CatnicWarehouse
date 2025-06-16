package com.example.catnicwarehouse.login.presentation

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.base.BaseFragment
import com.example.catnicwarehouse.databinding.FragmentLoginBinding
import com.example.catnicwarehouse.login.presentation.sealedClass.LoginEvent
import com.example.catnicwarehouse.login.presentation.sealedClass.LoginViewState
import com.example.catnicwarehouse.main.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class LoginFragment : BaseFragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LoginViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        observeLoginAction()

        binding.loginBtn.setOnClickListener {
            viewModel.onEvent(
                LoginEvent.LoginView(
                    userName = binding.userNameText.text.toString(),
                    password = binding.passwordText.text.toString()
                )
            )
        }
        val typeface = ResourcesCompat.getFont(requireContext(), R.font.robotoregular)
        binding.passwordTextInput.typeface = typeface
        binding.userNameTextInput.typeface = typeface
        return binding.root
    }



    private fun observeLoginAction() {
        viewModel.loginFlow.onEach { state ->
            when (state) {
                LoginViewState.Empty -> progressBarManager.dismiss()
                is LoginViewState.Error -> {
                    progressBarManager.dismiss()
                    state.errorMessage?.let { showErrorBanner(message = state.errorMessage) }
                }

                LoginViewState.Loading -> {
                    progressBarManager.show()
                }

                LoginViewState.LoginSuccessful -> {
                    progressBarManager.dismiss()
                    requireActivity().startActivity(
                        Intent(
                            requireActivity(), MainActivity::class.java
                        )
                    )
                    requireActivity().finish()
                }

                LoginViewState.Reset -> {
                    progressBarManager.dismiss()
                }

            }

        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }


}