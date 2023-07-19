package com.timing.game.ui.main

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.timing.game.R
import com.timing.game.core.library.soundClickListener
import com.timing.game.databinding.FragmentMainBinding
import com.timing.game.ui.other.ViewBindingFragment


class FragmentMain : ViewBindingFragment<FragmentMainBinding>(FragmentMainBinding::inflate) {
    private val sharedPrefs by lazy {
        requireActivity().getSharedPreferences("SHARED_PREFS", Context.MODE_PRIVATE)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setImage()
        binding.apply {
            startButton.soundClickListener {
                findNavController().navigate(R.id.action_fragmentMain_to_fragmentChoice)
            }
            exitButton.soundClickListener {
                findNavController().navigate(R.id.action_fragmentMain_to_dialogExit)
            }
            musicButton.soundClickListener {
                changeMusic()
            }
            privacy.setOnClickListener {
                val uri = Uri.parse("https://www.google.com")
                val intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intent)
            }
        }
    }

    private fun setImage() {
        val musicState = sharedPrefs.getBoolean("MUSIC", true)
        binding.musicButton.setImageResource(if (musicState) R.drawable.img_sound_on else R.drawable.img_sound_off)
    }

    private fun changeMusic() {
        val musicState = sharedPrefs.getBoolean("MUSIC", true)
        sharedPrefs.edit().putBoolean("MUSIC", !musicState).apply()
        setImage()
    }
}