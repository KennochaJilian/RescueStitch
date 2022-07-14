package fr.aranxa.codina.rescuestitch.user

import android.content.Context
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import fr.aranxa.codina.rescuestitch.R
import fr.aranxa.codina.rescuestitch.databinding.DialogUsernameBinding
import fr.aranxa.codina.rescuestitch.utils.AppUtils

class UsernameDialogFragment: DialogFragment() {

    private var _binding: DialogUsernameBinding? = null
    private lateinit var binding: DialogUsernameBinding

    private val userViewModel: UserViewModel by activityViewModels()


    companion object {
        const val TAG = "UsernameDialog"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = DialogUsernameBinding.inflate(inflater, container, false)
        binding = _binding!!

        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupButtons()
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        AppUtils().hideSystemUI(activity?.window)
    }

    private fun setupButtons() {
        binding.usernameDialogCloseButton.setOnClickListener {
            dismiss()
        }

        binding.usernameDialogCheckButton.setOnClickListener {
            val username = binding.usernameInput.text.toString()

            val prefs = requireActivity().getSharedPreferences(
                resources.getString(R.string.app_name),
                Context.MODE_PRIVATE
                )

            prefs.edit()
                .putString("username", username)
                .apply()

            userViewModel.username.value = username
            dismiss()
        }
    }

    private fun setupLayout() {
        val width = resources.getDimensionPixelSize(R.dimen.popup_width)
        val height = resources.getDimensionPixelSize(R.dimen.popup_height)
        dialog?.window?.setLayout(width, height)
    }

    override fun onResume() {
        super.onResume()
        setupLayout()
    }
}