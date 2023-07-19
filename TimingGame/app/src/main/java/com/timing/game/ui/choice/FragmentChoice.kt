package com.timing.game.ui.choice

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.timing.game.R
import com.timing.game.core.library.shortToast
import com.timing.game.core.library.soundClickListener
import com.timing.game.databinding.FragmentChoiceBinding
import com.timing.game.ui.other.ViewBindingFragment

class FragmentChoice : ViewBindingFragment<FragmentChoiceBinding>(FragmentChoiceBinding::inflate) {
    private val viewModel: ChoiceViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.playersAmount.observe(viewLifecycleOwner) {
            binding.apply {
                player1Button.setImageResource(if (it == 1) R.drawable.bg_selected_1 else R.drawable.bg_unselected_1)
                player2Button.setImageResource(if (it == 2) R.drawable.bg_selected_2 else R.drawable.bg_unselected_2)
                player3Button.setImageResource(if (it == 3) R.drawable.bg_selected_3 else R.drawable.bg_unselected_3)
                player4Button.setImageResource(if (it == 4) R.drawable.bg_selected_4 else R.drawable.bg_unselected_4)
            }
        }

        binding.player1Button.setOnClickListener {
            viewModel.resetMap()
            resetText()
            viewModel.setPlayersAmount(1)
        }
        binding.player2Button.setOnClickListener {
            viewModel.resetMap()
            resetText()
            viewModel.setPlayersAmount(2)
        }
        binding.player3Button.setOnClickListener {
            viewModel.resetMap()
            resetText()
            viewModel.setPlayersAmount(3)
        }
        binding.player4Button.setOnClickListener {
            viewModel.resetMap()
            resetText()
            viewModel.setPlayersAmount(4)
        }

        binding.buttonHome.soundClickListener {
            findNavController().popBackStack()
        }

        binding.buttonStart.soundClickListener {
            if (viewModel.playersAmount.value!! != viewModel.playersWithColors.value!!.size) {
                shortToast(requireContext(), "Select colors for all players")
            } else {
                val list = mutableListOf<Int>()
                viewModel.playersWithColors.value!!.forEach {
                    list.add(it.value)
                }
                findNavController().navigate(FragmentChoiceDirections.actionFragmentChoiceToFragmentGame(
                    Gson().toJson(list)
                ))
            }
        }

        binding.redColor.setOnClickListener {
            viewModel.addPlayerColor(1)
        }
        binding.blueColor.setOnClickListener {
            viewModel.addPlayerColor(2)
        }
        binding.yellowColor.setOnClickListener {
            viewModel.addPlayerColor(3)
        }
        binding.greenColor.setOnClickListener {
            viewModel.addPlayerColor(4)
        }

        viewModel.playersWithColors.observe(viewLifecycleOwner) {
            it.forEach { (key, value) ->
                when (value) {
                    1 -> {
                        binding.redColor.text = "PLAYER $key"
                    }

                    2 -> {
                        binding.blueColor.text = "PLAYER $key"
                    }

                    3 -> {
                        binding.yellowColor.text = "PLAYER $key"
                    }

                    else -> {
                        binding.greenColor.text = "PLAYER $key"
                    }
                }
            }

            if (it.isNotEmpty()) {
                val value = if (it.size == viewModel.playersAmount.value!!) {
                    it.size
                } else {
                    it.size + 1
                }
                binding.playerColorTextView.text = "PLAYER $value"
            } else {
                binding.playerColorTextView.text = "PLAYER 1"
            }
        }
    }

    private fun resetText() {
        binding.apply {
            listOf(redColor, blueColor, yellowColor, greenColor).forEach {
                it.text = ""
            }
        }
    }
}

