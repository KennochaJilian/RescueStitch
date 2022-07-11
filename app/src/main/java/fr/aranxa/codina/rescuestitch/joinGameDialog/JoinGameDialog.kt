package fr.aranxa.codina.rescuestitch.joinGameDialog


import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import fr.aranxa.codina.rescuestitch.MainActivity
import fr.aranxa.codina.rescuestitch.R
import fr.aranxa.codina.rescuestitch.network.payloads.PayloadType
import fr.aranxa.codina.rescuestitch.network.payloads.PlayerConnect
import fr.aranxa.codina.rescuestitch.network.payloads.PlayerConnectPayload
import fr.aranxa.codina.rescuestitch.user.UserViewModel
import fr.aranxa.codina.rescuestitch.network.SocketViewModel
import fr.aranxa.codina.rescuestitch.waitingRoom.WaitingRoomFragmentFragment


class JoinGameDialog(
    private val currentActivity: MainActivity
) : DialogFragment() {
    private val socketViewModel: SocketViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels()

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
            if(userViewModel.username.value != null){

            }
           goToWaitingRoom(view)
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
    private fun goToWaitingRoom(view:View){
//        val serverIp : String = view.findViewById<EditText>(R.id.adress_ip_input).toString()
//        val portValue : String = view.findViewById<EditText>(R.id.port_input).toString()
//        val port : Int = Integer.parseInt(portValue)
        val payload = PlayerConnectPayload(
            PayloadType.connect.toString(),
            PlayerConnect(
                userViewModel.username.value.toString(),
                socketViewModel.ipAddress.value.toString(),
                "8888"
            )
        ).toString()

//        socketViewModel.sendUDPData(payload,serverIp,port)
        currentActivity.loadFragment(WaitingRoomFragmentFragment(currentActivity), WaitingRoomFragmentFragment.TAG )
        dismiss()
    }


}