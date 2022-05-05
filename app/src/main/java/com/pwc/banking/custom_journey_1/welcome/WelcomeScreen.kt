package com.pwc.banking.custom_journey_1.welcome
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.pwc.banking.R
import com.pwc.banking.databinding.WelcomeScreenBinding

internal class WelcomeScreen : Fragment() {

    private val viewModel by viewModels<WelcomeViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = WelcomeScreenBinding.inflate(inflater, container, false)

        observeNavigation()

        with (binding) {

            oneScreenButton.setOnClickListener {
                viewModel.onOneScreenAction()
            }
            anotherScreenButton.setOnClickListener {
                viewModel.onAnotherScreenAction()
            }
        }

        return binding.root
    }

    private fun observeNavigation() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            for (event in viewModel.navigation) {
                val action = when (event) {
                    is WelcomeViewModel.Navigate.ToOneScreen ->
                        R.id.action_customJourney1_welcomeScreen_to_anotherScreen
                    is WelcomeViewModel.Navigate.ToAnotherScreen ->
                        R.id.action_customJourney1_welcomeScreen_to_anotherScreen
                }
                findNavController().navigate(action)
            }
        }
    }
}
