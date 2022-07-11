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
import fr.aranxa.codina.rescuestitch.MainActivity
import fr.aranxa.codina.rescuestitch.R

class UsernameDialogFragment(
    private val currentActivity: MainActivity
) : DialogFragment() {

    private val userViewModel: UserViewModel by activityViewModels()

    companion object {
        const val TAG = "UsernameDialog"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.username_dialog, container, false)
        setupButtons(view)
        return view
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        currentActivity.hideSystemUI()
    }

    private fun setupButtons(view: View) {
        view.findViewById<ImageView>(R.id.join_game_dialog_close_button).setOnClickListener {
            dismiss()
        }

        view.findViewById<ImageView>(R.id.join_game_dialog_check_button).setOnClickListener {
            val username = view
                .findViewById<TextView>(R.id.username_input)
                .text
                .toString()

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