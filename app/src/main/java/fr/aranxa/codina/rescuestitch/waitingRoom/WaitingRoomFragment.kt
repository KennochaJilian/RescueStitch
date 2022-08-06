package fr.aranxa.codina.rescuestitch.waitingRoom

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import fr.aranxa.codina.rescuestitch.game.GameViewModel
import fr.aranxa.codina.rescuestitch.R
import fr.aranxa.codina.rescuestitch.dataClasses.RoleType
import fr.aranxa.codina.rescuestitch.databinding.FragmentWaitingRoomBinding
import fr.aranxa.codina.rescuestitch.network.SocketViewModel
import fr.aranxa.codina.rescuestitch.network.payloads.GamePlayerPayload
import fr.aranxa.codina.rescuestitch.network.payloads.PayloadType
import fr.aranxa.codina.rescuestitch.network.payloads.PlayerUpdateStatusPayload
import fr.aranxa.codina.rescuestitch.user.UserViewModel

class WaitingRoomFragment : Fragment() {

    companion object {
        val TAG = "WaitingRoom"
    }

    private var _binding: FragmentWaitingRoomBinding? = null
    private lateinit var binding: FragmentWaitingRoomBinding
    private val args: WaitingRoomFragmentArgs by navArgs()

    private val socketViewModel: SocketViewModel by activityViewModels()
    private val gameViewModel: GameViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentWaitingRoomBinding.inflate(inflater, container, false)
        binding = _binding!!
        setupIPAdress()
        setupReadyButton()
        setupLauchGameButton()

//        init game
        socketViewModel.ipAddress.observe(viewLifecycleOwner) { ipAdress ->
            if (ipAdress !== null) {
                if (args.origin == WaitingRoomOriginTypes.mainMenu.toString()) {
                    initGame(ipAdress)
                }
            }
        }

//        update Player in Recycler view
        val playersRecyclerView = binding.playersWaitingList

        gameViewModel.currentGame.observe(viewLifecycleOwner) { currentGame ->
            if (currentGame != null) {
                playersRecyclerView.adapter =
                    WaitingRoomAdapter(requireContext(), currentGame.players)

                if (currentGame.players.find { player -> !player.status } == null && currentGame.players.size > 1) {
                    gameViewModel.gameIsLauncheable.postValue(true)
                } else {
                    gameViewModel.gameIsLauncheable.postValue(false)
                }
                updateButtons()

                if (currentGame.game.status == "started") {
                    launchGame()
                }
            }
        }

//        update Player in Game
        gameViewModel.addPlayer.observe(viewLifecycleOwner) {
            if (it == true) {
                updatePlayerInGame()
                gameViewModel.addPlayer.postValue(false)
            }
        }

//      get request from SocketViewModel
        listenPayload()
        return binding.root
    }

    private fun updateButtons() {
        val currentGame = gameViewModel.currentGame.value
        val currentPlayer = currentGame?.players?.find { player ->
            player.name == userViewModel.username.value &&
                    player.ipAddress == socketViewModel.ipAddress.value
        }
        if (currentPlayer != null) {
            if (currentPlayer.status) {
                binding.readyButton.setBackgroundResource(R.drawable.rect_rounded_active_button)
            } else {
                binding.readyButton.setBackgroundResource(R.drawable.rect_rounded_disabled_button)
            }
        }

        if (currentGame != null) {
            if ((currentGame.game.role) == RoleType.client.toString()) {
                binding.launchGameButton.visibility = INVISIBLE
            }
        }


    }

    private fun setupLauchGameButton() {

        gameViewModel.gameIsLauncheable.observe(viewLifecycleOwner) { isLauncheable ->
            if (isLauncheable) {
                binding.launchGameButton.setBackgroundResource(R.drawable.rect_rounded_active_button)
            } else {
                binding.launchGameButton.setBackgroundResource(R.drawable.rect_rounded_disabled_button)
            }
            binding.launchGameButton.setOnClickListener {
                if (isLauncheable) {
                    launchGame()
                    gameViewModel.launchGame()
                } else {
                    Toast.makeText(
                        context,
                        R.string.waiting_room_game_is_not_launcheable,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

    }

    private fun setupReadyButton() {
        binding.readyButton.setOnClickListener {
            val currentGame = gameViewModel.currentGame.value
            val ipAdress = socketViewModel.ipAddress.value
            val currentPlayer = currentGame?.players?.find { player ->
                player.name == userViewModel.username.value &&
                        player.ipAddress == ipAdress
            }

            if (currentPlayer != null) {
                currentPlayer.status = !currentPlayer.status

                gameViewModel.updatePlayerStatus(currentPlayer)

//                send new status to all player

                val payload = PlayerUpdateStatusPayload(
                    data = currentPlayer
                ).jsonEncodeToString()

                for (player in currentGame.players) {
                    socketViewModel.sendUDPData(
                        payload,
                        player.ipAddress,
                        8888
                    )
                }

            }

        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initGame(ipAdress: String) {
        gameViewModel.initGame(
            RoleType.server.toString(),
            ipAdress,
            socketViewModel.ipAddress.value!!,
            userViewModel.username.value.toString()
        )
    }

    private fun updatePlayerInGame() {
        val payload = gameViewModel.currentGame.value?.players?.let {
            GamePlayerPayload(
                PayloadType.players.toString(),
                it
            ).jsonEncodeToString()
        }

        for (player in gameViewModel.currentGame.value?.players!!) {
            if (socketViewModel.ipAddress.value != player.ipAddress) {
                if (payload != null) {
                    socketViewModel.sendUDPData(payload, player.ipAddress, player.port)
                }
            }
        }
    }

    private fun setupIPAdress() {
        socketViewModel.ipAddress.observe(viewLifecycleOwner) { ipAdress ->
            binding.textIpAdress.text = ipAdress
        }
    }

    private fun launchGame() {
        findNavController().navigate(R.id.action_waitingRoomFragment_to_gameFragment)

    }

    private fun listenPayload() {
        socketViewModel.payload.observe(viewLifecycleOwner) { payload ->
            if (payload != null) {
                gameViewModel.handlePayload(payload)
            }
        }
    }


}