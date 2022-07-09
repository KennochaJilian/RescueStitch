package fr.aranxa.codina.rescuestitch.joinGameDialog


import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import fr.aranxa.codina.rescuestitch.MainActivity
import fr.aranxa.codina.rescuestitch.R
import fr.aranxa.codina.rescuestitch.waitingRoom.WaitingRoomFragmentFragment


class JoinGameDialog(
    private val currentActivity: MainActivity
) : DialogFragment() {

    companion object {
        const val TAG = "JoinGameDialog"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.join_game_dialog, container, false)
        setupButtons(view)
        return view
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        currentActivity.hideSystemUI()
    }

    private fun setupButtons(view : View) {
        view.findViewById<ImageView>(R.id.join_game_dialog_close_button).setOnClickListener{
            dismiss()
        }
        view.findViewById<ImageView>(R.id.join_game_dialog_check_button).setOnClickListener {
            currentActivity.loadFragment(WaitingRoomFragmentFragment(currentActivity), WaitingRoomFragmentFragment.TAG )
            dismiss()
        }
    }
    private fun setupLayout(){
        val width = resources.getDimensionPixelSize(R.dimen.popup_width)
        val height = resources.getDimensionPixelSize(R.dimen.popup_height)
        dialog?.window?.setLayout(width,height)
    }

    override fun onResume() {
        super.onResume()
        setupLayout()
    }


}