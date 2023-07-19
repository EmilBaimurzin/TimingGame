package com.timing.game.ui.game

import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.gson.Gson
import com.timing.game.R
import com.timing.game.core.library.safe
import com.timing.game.core.library.soundClickListener
import com.timing.game.databinding.FragmentGameBinding
import com.timing.game.domain.game.Color
import com.timing.game.domain.game.GameItem
import com.timing.game.ui.choice.FragmentChoiceDirections
import com.timing.game.ui.other.ViewBindingFragment
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FragmentGame : ViewBindingFragment<FragmentGameBinding>(FragmentGameBinding::inflate) {
    private val sharedPrefs by lazy {
        requireActivity().getSharedPreferences("SHARED_PREFS", Context.MODE_PRIVATE)
    }
    private val args: FragmentGameArgs by navArgs()
    private lateinit var realPlayers: List<Double>
    private lateinit var viewModel: GameViewModel
    private lateinit var mediaPlayer: MediaPlayer
    private val callbackViewModel: CallbackViewModel by activityViewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        realPlayers = Gson().fromJson(args.players, List::class.java) as List<Double>
        mediaPlayer = MediaPlayer.create(requireContext(), R.raw.sound_sh)

        viewModel = ViewModelProvider(
            this,
            GameViewModelFactory(realPlayers.map { getColorById(it.toInt()) })
        )[GameViewModel::class.java]

        callbackViewModel.callback = {
            viewModel.startGame()
            viewModel.pauseState = false
        }
        viewModel.callBack = {
            click(it)
        }

        viewModel.callBackSound = {
            lifecycleScope.launch {
                if (sharedPrefs.getBoolean("MUSIC", true)) {
                    mediaPlayer.seekTo(0)
                    mediaPlayer.start()
                    delay(3000)
                    mediaPlayer.pause()
                }
            }
        }

        binding.homeButton.soundClickListener {
            findNavController().popBackStack(R.id.fragmentMain, false, false)
        }

        binding.restartButton.soundClickListener {
            findNavController().popBackStack(R.id.fragmentMain, false, false)
            findNavController().navigate(R.id.action_fragmentMain_to_fragmentChoice)
            findNavController().navigate(FragmentChoiceDirections.actionFragmentChoiceToFragmentGame(args.players))
        }

        binding.pauseButton.soundClickListener {
            mediaPlayer.pause()
            viewModel.stopGame()
            viewModel.pauseState = true
            findNavController().navigate(R.id.action_fragmentGame_to_dialogPause)
        }
        binding.buttonRed.setOnClickListener {
            if (realPlayers.contains(getIdByColor(Color.RED).toDouble())) {
                click(Color.RED)
                viewModel.buttonClick(Color.RED)
            }
        }
        binding.buttonBlue.setOnClickListener {
            if (realPlayers.contains(getIdByColor(Color.BLUE).toDouble())) {
                click(Color.BLUE)
                viewModel.buttonClick(Color.BLUE)
            }
        }
        binding.buttonGreen.setOnClickListener {
            if (realPlayers.contains(getIdByColor(Color.GREEN).toDouble())) {
                click(Color.GREEN)
                viewModel.buttonClick(Color.GREEN)
            }
        }
        binding.buttonYellow.setOnClickListener {
            if (realPlayers.contains(getIdByColor(Color.YELLOW).toDouble())) {
                click(Color.YELLOW)
                viewModel.buttonClick(Color.YELLOW)
            }
        }
        viewModel.blueScores.observe(viewLifecycleOwner) {
            binding.blueScores.text = it.toString()
        }
        viewModel.redScores.observe(viewLifecycleOwner) {
            binding.redScores.text = it.toString()
        }
        viewModel.yellowScores.observe(viewLifecycleOwner) {
            binding.yellowScores.text = it.toString()
        }
        viewModel.greenScores.observe(viewLifecycleOwner) {
            binding.greenScores.text = it.toString()
        }
        viewModel.imageValue.observe(viewLifecycleOwner) {
            val value = when (it) {
                GameItem.SYMBOL_1 -> R.drawable.img_symbol01
                GameItem.SYMBOL_2 -> R.drawable.img_symbol02
                GameItem.SYMBOL_3 -> R.drawable.img_symbol03
                GameItem.SYMBOL_4 -> R.drawable.img_symbol04
                GameItem.SYMBOL_5 -> R.drawable.img_symbol05
                else -> R.drawable.img_bomb
            }
            binding.gameImage.setImageResource(value)
        }
        viewModel.timer.observe(viewLifecycleOwner) {
            binding.firstTimer.text = it.toString()
            binding.secondTimer.text = it.toString()
            if (it == 0) {
                viewModel.endTimer()
                end()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            if (viewModel.timer.value!! > 0 && !viewModel.pauseState) {
                viewModel.startGame()
            }
        }
    }

    private fun getColorById(value: Int): Color {
        return when (value) {
            1 -> Color.RED
            2 -> Color.BLUE
            3 -> Color.YELLOW
            else -> Color.GREEN
        }
    }

    private fun end() {
        viewModel.stopGame()
        val winList = listOf(
            viewModel.greenScores.value!!,
            viewModel.blueScores.value!!,
            viewModel.redScores.value!!,
            viewModel.yellowScores.value!!,
        )
        val winner = when (winList.max()) {
            viewModel.greenScores.value!! -> Color.GREEN
            viewModel.redScores.value!! -> Color.RED
            viewModel.blueScores.value!! -> Color.BLUE
            else -> Color.YELLOW
        }
        findNavController().navigate(FragmentGameDirections.actionFragmentGameToDialogEnd(winner, args.players))
    }

    private fun getIdByColor(value: Color): Int {
        return when (value) {
            Color.RED -> 1
            Color.GREEN -> 2
            Color.BLUE -> 3
            Color.YELLOW -> 4
        }
    }

    private fun click(value: Color) {
        when (value) {
            Color.RED -> {
                lifecycleScope.launch {
                    safe {
                        binding.redFinger.visibility = View.GONE
                        binding.redFingerClicked.visibility = View.VISIBLE
                    }
                    delay(100)
                    safe {
                        binding.redFinger.visibility = View.VISIBLE
                        binding.redFingerClicked.visibility = View.GONE
                    }
                }
            }

            Color.GREEN -> {
                lifecycleScope.launch {
                    safe {
                        binding.greenFinger.visibility = View.GONE
                        binding.greenFingerClicked.visibility = View.VISIBLE
                    }
                    delay(100)
                    safe {
                        binding.greenFinger.visibility = View.VISIBLE
                        binding.greenFingerClicked.visibility = View.GONE
                    }
                }
            }

            Color.BLUE -> {
                lifecycleScope.launch {
                    safe {
                        binding.blueFinger.visibility = View.GONE
                        binding.blueFingerClicked.visibility = View.VISIBLE
                    }
                    delay(100)
                    safe {
                        binding.blueFinger.visibility = View.VISIBLE
                        binding.blueFingerClicked.visibility = View.GONE
                    }
                }
            }

            Color.YELLOW -> {
                lifecycleScope.launch {
                    safe {
                        binding.yellowFinger.visibility = View.GONE
                        binding.yellowFingerClicked.visibility = View.VISIBLE
                    }
                    delay(100)
                    safe {
                        binding.yellowFinger.visibility = View.VISIBLE
                        binding.yellowFingerClicked.visibility = View.GONE
                    }
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        mediaPlayer.pause()
        viewModel.stopGame()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }
}