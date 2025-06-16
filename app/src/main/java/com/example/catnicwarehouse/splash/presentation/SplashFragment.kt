package com.example.catnicwarehouse.splash.presentation

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.catnicwarehouse.R
import com.example.catnicwarehouse.databinding.FragmentSplashBinding
import com.example.catnicwarehouse.main.main.MainActivity
import com.example.catnicwarehouse.splash.presentation.sealedClass.SplashEvent
import com.example.catnicwarehouse.splash.presentation.sealedClass.SplashViewState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class SplashFragment : Fragment() {
    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        progressBarAnimation()
        return binding.root
    }

    private fun progressBarAnimation() {
        lifecycleScope.launch {
            withContext(Dispatchers.Main) {
                val animator = ValueAnimator.ofInt(0, 100).apply {
                    duration = 2000 // Duration of 2 seconds
                    addUpdateListener { animation ->
                        val progress = animation.animatedValue as Int
                        binding.progressBar.progress = progress
                    }
                    addListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            viewModel.onEvent(SplashEvent.CheckUserConnected)
                            observeUserState()
                        }
                    })
                }
                animator.start()
            }
        }

    }

    private fun observeUserState() {
        viewModel.splashFlow.onEach { state ->
            when (state) {
                SplashViewState.Empty -> {}
                is SplashViewState.UserExists -> {
                    if (state.isUserExisting) {
                        requireActivity().startActivity(
                            Intent(
                                requireActivity(),
                                MainActivity::class.java
                            )
                        )
                        requireActivity().finish()
                    } else {
                        findNavController().navigate(R.id.action_splashFragment_to_loginFragment)
                    }
                }
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }
}

