package com.timing.game.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.navigation.fragment.findNavController
import com.timing.game.R
import com.timing.game.core.library.ViewBindingDialog
import com.timing.game.core.library.soundClickListener
import com.timing.game.databinding.DialogExitBinding

class DialogExit: ViewBindingDialog<DialogExitBinding>(DialogExitBinding::inflate) {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(requireContext(), R.style.Dialog_No_Border)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog!!.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                findNavController().popBackStack()
                true
            } else {
                false
            }
        }
        binding.buttonNo.soundClickListener {
            findNavController().popBackStack()
        }
        binding.buttonYes.soundClickListener {
            requireActivity().finish()
        }
    }
}