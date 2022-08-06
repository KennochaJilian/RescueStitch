package fr.aranxa.codina.rescuestitch.game

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.github.nisrulz.sensey.Sensey
import com.github.nisrulz.sensey.ShakeDetector.ShakeListener
import fr.aranxa.codina.rescuestitch.R
import fr.aranxa.codina.rescuestitch.dataClasses.*
import fr.aranxa.codina.rescuestitch.databinding.FragmentGameBinding
import fr.aranxa.codina.rescuestitch.network.SocketViewModel
import fr.aranxa.codina.rescuestitch.network.payloads.*
import java.util.*


class GameFragment : Fragment() {

    private var _binding: FragmentGameBinding? = null
    private lateinit var binding: FragmentGameBinding

    private val socketViewModel: SocketViewModel by activityViewModels()
    private val gameViewModel: GameViewModel by activityViewModels()


    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGameBinding.inflate(inflater, container, false)
        binding = _binding!!

        Sensey.getInstance().init(context);

        val game = gameViewModel.currentGame.value
        if (game != null) {
            displayShipIntegrity(game.game.shipIntegrity)
            displayTurn(game.game.turn)
            if (game.game.role == RoleType.server.toString()) {
                for (player in game.players) {
                    if (player.ipAddress != game.game.ipAddress) {
                        socketViewModel.sendUDPData(
                            GameStartPayload().jsonEncodeToString(),
                            player.ipAddress,
                            8888
                        )
                    }
                }
            }

        }

        gameViewModel.currentGame.observe(viewLifecycleOwner) { currentGame ->
            if (currentGame != null) {
                clearFragment()
                displayShipIntegrity(currentGame.game.shipIntegrity)
                displayTurn(currentGame.game.turn)
            }

        }

        gameViewModel.currentOperations.observe(viewLifecycleOwner) { operations ->
            if (operations != null && operations.isNotEmpty()) {
                val currentOperations = operations.toMutableList()
                val i = (0..currentOperations.size - 1).random()
                gameViewModel.currentOperation.postValue(
                    currentOperations.removeAt(i)
                )
                if (game != null) {
                    for (player in game.players) {
                        if (player.ipAddress != socketViewModel.ipAddress.value) {
                            val y = (0..currentOperations.size - 1).random()
                            socketViewModel.sendUDPData(
                                GameOperationPayload(
                                    type = PayloadType.operation.toString(),
                                    data = currentOperations.removeAt(y)
                                ).jsonEncodeToString(),
                                player.ipAddress,
                                8888
                            )
                        }
                    }
                }
            }

        }
        gameViewModel.currentOperation.observe(viewLifecycleOwner) { operation ->
            if (operation != null) {
                displayOperation(operation)
            }
        }

        binding.validateOperationButton.setOnClickListener { it ->
            val registeredEvents = gameViewModel.registeredEvents.value
            val resultService = registeredEvents?.let { it1 ->
                gameViewModel.currentOperation.value?.result?.let { it2 ->
                    ManageResultService(
                        it1,
                        it2
                    )
                }
            }
            if (resultService != null) {
                gameViewModel.currentOperationResult.postValue(resultService.operationIsSuccessful())
            }


        }

        gameViewModel.currentOperationResult.observe(viewLifecycleOwner) { isSuccessful ->
            if (isSuccessful != null) {
                manageEndOperation(isSuccessful)
            }
        }

        gameViewModel.currentFinishedOperationResult.observe(viewLifecycleOwner) { results ->
            if (results != null && results.size > 0) {
                manageResults(results)
            }
        }

        gameViewModel.endedGame.observe(viewLifecycleOwner) { isEnded ->
            if (isEnded) {
                findNavController().navigate(R.id.action_gameFragment_to_endGameFragment2)
            }
        }

        listenPayload()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val game = gameViewModel.currentGame.value
        if (game != null && game.game.role == RoleType.server.toString()) {
            gameViewModel.updateOperations(game.game.turn, game.players.size)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Sensey.getInstance().stop()
    }

    private fun displayTurn(turn: Int) {
        binding.textNbTurn.text = turn.toString()
    }

    private fun displayOperation(operation: Operation) {
        displayRole(operation.role, operation.id)
        when (operation.role) {
            OperationRoleType.operator.toString() -> {
                operation.elements?.filter { it.type == ElementType.button.toString() }
                    ?.let { displayButtons(it) }
                manageTourDuration(operation.duration!!)
                operation.elements?.filter { it.type == ElementType.switch.toString() }
                    ?.let { displaySwitches(it) }
                if (operation.elements?.any { it.type == ElementType.shake.toString() } == true) {
                    manageShakeListener()
                }
            }
            OperationRoleType.intructor.toString() -> {
                manageTourDuration(operation.duration!!)
                displayInstruction(operation.description!!)
            }
        }
    }

    private fun displayShipIntegrity(integrity: Int) {
        binding.shipIntegrityProgressBar.progress = integrity
    }

    private fun displayRole(role: String, operatorId: String?) {
        when (role) {
            OperationRoleType.spectactor.toString() -> {
                binding.operationRoleText.text = getString(R.string.game_fragment_spectator_role)
            }
            OperationRoleType.intructor.toString() -> {
                binding.operationRoleText.text =
                    getString(R.string.game_fragment_instructor_role, operatorId)
            }
            OperationRoleType.operator.toString() -> binding.operationRoleText.text = operatorId!!
            else -> {}
        }
    }

    private fun displayButtons(buttonsList: List<Element>) {
        val buttonsRecyclerView = binding.buttonsOperationList
        buttonsRecyclerView.adapter = ButtonsOperationAdapter(
            requireContext(),
            buttonsList,
            ElementOnClickListener { id ->
                val registeredEvents = gameViewModel.registeredEvents.value
                registeredEvents?.buttons?.add(id)
                gameViewModel.registeredEvents.postValue(
                    registeredEvents
                )
            }
        )
        binding.buttonsOperationList.visibility = VISIBLE
    }

    private fun displaySwitches(switchesList: List<Element>) {
        val switchesRecyclerView = binding.switchesOperationList
        switchesRecyclerView.adapter = SwitchesOperationAdapter(
            requireContext(),
            switchesList,
            ElementOnClickListener { id ->
                val registeredEvents = gameViewModel.registeredEvents.value
                registeredEvents?.switches?.add(id)
                gameViewModel.registeredEvents.postValue(
                    registeredEvents
                )
            }
        )
        binding.switchesOperationList.visibility = VISIBLE
    }

    private fun displayInstruction(description: String) {
        binding.operationDescriptionText.visibility = VISIBLE
        binding.operationDescriptionText.text = description
    }


    private fun manageTourDuration(durationTurn: Int) {
        gameViewModel.timer.observe(viewLifecycleOwner) { it ->
            binding.textTimer.text = it.toString()
        }
        var timer = durationTurn
        Timer().scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                if (timer <= 0) {
                    this.cancel()
                    val registeredEvents = gameViewModel.registeredEvents.value

                    val resultService = registeredEvents?.let { it1 ->
                        gameViewModel.currentOperation.value?.result?.let { it2 ->
                            ManageResultService(
                                it1,
                                it2
                            )
                        }
                    }
                    if (resultService != null) {
                        gameViewModel.currentOperationResult.postValue(resultService.operationIsSuccessful())
                    }
                } else {
                    timer--
                    gameViewModel.timer.postValue(timer)
                }
            }
        }, 1000, 1000)
        gameViewModel.timer.postValue(durationTurn)

    }

    private fun manageShakeListener() {
        val shakeListener: ShakeListener = object : ShakeListener {
            override fun onShakeDetected() {
                val registeredEvents = gameViewModel.registeredEvents.value
                registeredEvents?.was_shaken = true
                gameViewModel.registeredEvents.postValue(
                    registeredEvents
                )
            }

            override fun onShakeStopped() {
                return
            }
        }
        Sensey.getInstance().startShakeDetection(shakeListener)
    }

    fun manageEndOperation(isSuccessful: Boolean) {
        val currentOperationRole = gameViewModel.currentOperation.value?.role
        val currentOperationId = gameViewModel.currentOperation.value?.id
        val currentGame = gameViewModel.currentGame.value?.game
//        check if current player is operator
        if (currentOperationRole != null && currentOperationRole == OperationRoleType.operator.toString()) {
            if (currentGame != null && currentOperationId != null) {
//                check if current player is client to send operation result to server else keep in live data

                if (currentGame.role == RoleType.client.toString()) {
                    socketViewModel.sendUDPData(
                        data = GameEndOperationPayload(
                            type = PayloadType.finish.toString(),
                            data = GameEndOperationData(
                                id = currentOperationId,
                                success = isSuccessful
                            )
                        ).jsonEncodeToString(),
                        currentGame.ipAddress,
                        8888
                    )
                } else {
                    val result = gameViewModel.currentFinishedOperationResult.value
                    result?.add(isSuccessful)
                    gameViewModel.currentFinishedOperationResult.postValue(result)
                }
            }
        }
    }

    fun manageResults(results: MutableList<Boolean>) {
        val currentGame = gameViewModel.currentGame.value
        if (currentGame != null) {
            val nbPlayers = currentGame.players.size

            if (results.size == nbPlayers / 2 || results.size == (nbPlayers - 1) / 2) {
                val nbFailure = results.count { it == false }
                currentGame.game.shipIntegrity = currentGame.game.shipIntegrity - (nbFailure * 40)

                if (currentGame.game.shipIntegrity <= 0) {
                    findNavController().navigate(R.id.action_gameFragment_to_endGameFragment2)
                }
                for (player in currentGame.players) {
                    if (player.ipAddress != socketViewModel.ipAddress.value) {
                        socketViewModel.sendUDPData(
                            data = ShipIntegrityPayload(
                                type = PayloadType.integrity.toString(),
                                data = ShipIntegrityData(
                                    integrity = currentGame.game.shipIntegrity
                                )
                            ).jsonEncodeToString(),
                            player.ipAddress,
                            8888
                        )
                    }
                }

                currentGame.game.turn++
                gameViewModel.currentGame.postValue(currentGame)
                clearFragment()
                cleanCurrentTurnLiveDatas()
                gameViewModel.updateOperations(currentGame.game.turn, nbPlayers)

            }

        }
    }

    fun clearFragment() {
        binding.validateOperationButton.visibility = INVISIBLE
        binding.operationDescriptionText.visibility = INVISIBLE
        binding.buttonsOperationList.visibility = INVISIBLE
        binding.switchesOperationList.visibility = INVISIBLE
    }

    fun cleanCurrentTurnLiveDatas() {
        gameViewModel.currentOperation.postValue(null)
        gameViewModel.currentFinishedOperationResult.postValue(mutableListOf<Boolean>())
        gameViewModel.registeredEvents.postValue(RegisteredEvents())
    }

    private fun listenPayload() {
        socketViewModel.payload.observe(viewLifecycleOwner) { payload ->
            if (payload != null) {
                gameViewModel.handlePayload(payload)
            }
        }
    }
}
