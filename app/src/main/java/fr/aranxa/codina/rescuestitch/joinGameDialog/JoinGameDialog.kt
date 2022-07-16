package fr.aranxa.codina.rescuestitch.joinGameDialog


import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import fr.aranxa.codina.rescuestitch.GameViewModel
import fr.aranxa.codina.rescuestitch.R
import fr.aranxa.codina.rescuestitch.dataClasses.Player
import fr.aranxa.codina.rescuestitch.dataClasses.RoleType
import fr.aranxa.codina.rescuestitch.databinding.DialogJoinGameBinding
import fr.aranxa.codina.rescuestitch.network.SocketViewModel
import fr.aranxa.codina.rescuestitch.network.payloads.PayloadType
import fr.aranxa.codina.rescuestitch.network.payloads.PlayerConnectPayload
import fr.aranxa.codina.rescuestitch.user.UserViewModel
import fr.aranxa.codina.rescuestitch.utils.AppUtils
import fr.aranxa.codina.rescuestitch.waitingRoom.WaitingRoomOriginTypes


class JoinGameDialog : DialogFragment() {

    private var _binding: DialogJoinGameBinding? = null
    private lateinit var binding: DialogJoinGameBinding

    private val socketViewModel: SocketViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels()
    private val gameViewModel: GameViewModel by activityViewModels()

    companion object {
        const val TAG = "JoinGameDialog"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DialogJoinGameBinding.inflate(inflater, container, false)
        binding = _binding!!
        return _binding?.root
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupButtons()
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        AppUtils().hideSystemUI(activity?.window)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupButtons() {
        binding.joinGameDialogCloseButton.setOnClickListener {
            dismiss()
        }
        binding.joinGameDialogCheckButton.setOnClickListener {
            goToWaitingRoom()
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


    @RequiresApi(Build.VERSION_CODES.O)
    private fun goToWaitingRoom() {
//        val serverIp: String = binding.adressIpInput.text.toString()
        val serverIp = "192.168.1.100"

        val portValue: String = binding.portInput.text.toString()
//
//        if (serverIp == "" && portValue == "") {
//            Toast.makeText(
//                context,
//                R.string.join_game_dialogue_no_ip_and_port_toast,
//                Toast.LENGTH_LONG
//            ).show()
//            return
//        }
//
//        val port: Int = Integer.parseInt(portValue)

        gameViewModel.initGame(
            RoleType.client.toString(),
            serverIp,
            socketViewModel.ipAddress.value!!,
            userViewModel.username.value!!
        )

        gameViewModel.currentGame.observe(viewLifecycleOwner) { currentGame ->
            if (currentGame != null) {
                val payload = PlayerConnectPayload(
                    PayloadType.connect.toString(),
                    Player(
                        name = userViewModel.username.value.toString(),
                        ipAddress = socketViewModel.ipAddress.value.toString(),
                        port = 8888
                    )
                ).jsonEncodeToString()

//        socketViewModel.sendUDPData(payload, serverIp, port)
                socketViewModel.sendUDPData(payload, serverIp, 8888)

                val action = JoinGameDialogDirections
                    .actionJoinGameDialogToWaitingRoomFragment()
                    .setOrigin(WaitingRoomOriginTypes.joinGameDialog.toString())

                findNavController().navigate(action)
                dismiss()
            }
        }
    }


}