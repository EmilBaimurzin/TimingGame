package com.timing.game.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.timing.game.R
import com.timing.game.core.library.ViewBindingDialog
import com.timing.game.core.library.soundClickListener
import com.timing.game.databinding.DialogEndBinding
import com.timing.game.ui.choice.FragmentChoiceDirections

class DialogEnd: ViewBindingDialog<DialogEndBinding>(DialogEndBinding::inflate) {
    private val args: DialogEndArgs by navArgs()
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(requireContext(), R.style.Dialog_No_Border)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog!!.setCancelable(false)
        dialog!!.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                findNavController().popBackStack(R.id.fragmentMain, false, false)
                true
            } else {
                false
            }
        }

        binding.winTextView.text = args.win.name + " " + "WINS!"

        binding.buttonNo.soundClickListener {
            findNavController().popBackStack(R.id.fragmentMain, false, false)
        }

        binding.buttonYes.soundClickListener {
            findNavController().popBackStack(R.id.fragmentMain, false, false)
            findNavController().navigate(R.id.action_fragmentMain_to_fragmentChoice)
            findNavController().navigate(FragmentChoiceDirections.actionFragmentChoiceToFragmentGame(args.players))
        }
    }
}