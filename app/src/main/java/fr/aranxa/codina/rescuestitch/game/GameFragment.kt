package fr.aranxa.codina.rescuestitch.game

import android.content.Context
import android.graphics.PorterDuff
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Vibrator
import android.util.Log
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
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import fr.aranxa.codina.rescuestitch.R
import fr.aranxa.codina.rescuestitch.dataClasses.*
import fr.aranxa.codina.rescuestitch.databinding.FragmentGameBinding
import fr.aranxa.codina.rescuestitch.network.SocketViewModel
import fr.aranxa.codina.rescuestitch.network.payloads.*
import fr.aranxa.codina.rescuestitch.waitingRoom.VideoFragmentOriginType
import java.util.*


class GameFragment : Fragment() {

    private var _binding: FragmentGameBinding? = null
    private lateinit var binding: FragmentGameBinding

    private lateinit var vibrator: Vibrator

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

        setupBackButton()

        Sensey.getInstance().init(context);

        vibrator = activity?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        val game = gameViewModel.currentGame.value
        if (game != null) {
            displayShipIntegrity(game.game.shipIntegrity)
            displayTurn(game.game.turn)
        }

        if (game != null && game.game.role == RoleType.server.toString()) {
            gameViewModel.updateOperations(game.game.turn, game.players.size)
        }

        gameViewModel.currentGame.observe(viewLifecycleOwner) { currentGame ->
            if (currentGame != null) {
                displayShipIntegrity(currentGame.game.shipIntegrity)
                displayTurn(currentGame.game.turn)
                displayFire(currentGame.game.shipIntegrity)
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
                val action = GameFragmentDirections.actionGameFragmentToVideoShipFragment(
                    VideoFragmentOriginType.game.toString()
                )
                findNavController().navigate(action)
            }
        }

        clearFragment()
        listenPayload()

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        Sensey.getInstance().stop()
    }

    private fun setupBackButton() {
        binding.back.setOnClickListener {
            val game = gameViewModel.currentGame.value
            if(game != null){
                gameViewModel.endGame(game.game, GameStatusType.unfinished.toString())
            }
            gameViewModel.resetLiveData()
            findNavController().navigate(R.id.action_gameFragment_to_mainMenuFragment)

        }
    }

    private fun displayTurn(turn: Int) {
        binding.textNbTurn.text = turn.toString()
    }

    private fun displayOperation(operation: Operation) {
        clearFragment()
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
                binding.uselessPanel.visibility = VISIBLE
                binding.switchesOperationList.visibility = VISIBLE
                binding.buttonsOperationList.visibility = VISIBLE

            }
            OperationRoleType.intructor.toString() -> {
                displayInstruction(operation.description!!)
                manageTourDuration(operation.duration!!)
            }
        }
    }

    private fun displayShipIntegrity(integrity: Int) {
        val progressbar = binding.shipIntegrityProgressBar
        progressbar.progress = integrity
        if (integrity <= 30) {
            val color = resources.getColor(R.color.ship_red)
            progressbar.getIndeterminateDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);
            progressbar.getProgressDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);

        }
    }

    private fun displayRole(role: String, operatorId: String?) {
        binding.operationRoleText.visibility = VISIBLE
        Log.d("ROLE", role)
        when (role) {
            OperationRoleType.spectactor.toString() -> {
                binding.operationRoleText.text = getString(R.string.game_fragment_spectator_role)
            }
            OperationRoleType.intructor.toString() -> {
                Log.d("ROLE", "Ok j'affiche le role instructeur")
                binding.operationRoleText.text =
                    getString(R.string.game_fragment_instructor_role, operatorId)
            }
            OperationRoleType.operator.toString() -> binding.operationRoleText.text = operatorId!!
            else -> {}
        }
    }

    private fun displayButtons(buttonsList: List<Element>) {
        binding.buttonsOperationList.visibility = VISIBLE

        val layoutManager = FlexboxLayoutManager(requireContext())
        layoutManager.flexWrap = FlexWrap.WRAP
        layoutManager.flexDirection = FlexDirection.ROW
        layoutManager.justifyContent = JustifyContent.SPACE_AROUND

        val buttonsRecyclerView = binding.buttonsOperationList
        buttonsRecyclerView.layoutManager = layoutManager
        buttonsRecyclerView.adapter = ButtonsOperationAdapter(
            requireContext(),
            buttonsList,
            ElementOnClickListener { id ->
                val registeredEvents = gameViewModel.registeredEvents.value
                registeredEvents?.buttons?.add(id)
                gameViewModel.registeredEvents.postValue(
                    registeredEvents
                )
                vibrator.vibrate(50)

            }
        )

    }

    private fun displaySwitches(switchesList: List<Element>) {
        binding.switchesOperationList.visibility = VISIBLE
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
                vibrator.vibrate(50)
            }
        )

    }

    private fun displayInstruction(description: String) {
        binding.operationDescriptionText.visibility = VISIBLE
        binding.boardPanelBackground.visibility = VISIBLE
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
        var mediaPlayer = MediaPlayer.create(context, R.raw.shake_sound)
        val shakeListener: ShakeListener = object : ShakeListener {
            override fun onShakeDetected() {
                val registeredEvents = gameViewModel.registeredEvents.value
                registeredEvents?.was_shaken = true
                gameViewModel.registeredEvents.postValue(
                    registeredEvents
                )
                vibrator.vibrate(50)
                mediaPlayer.start()
            }

            override fun onShakeStopped() {
                Sensey.getInstance().stop()
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
        var mediaPlayer = MediaPlayer.create(context, R.raw.ship_damage)
        val currentGame = gameViewModel.currentGame.value
        if (currentGame != null) {
            val nbPlayers = currentGame.players.size

            if (results.size == nbPlayers / 2 || results.size == (nbPlayers - 1) / 2) {
                val nbFailure = results.count { it == false }
                currentGame.game.shipIntegrity = currentGame.game.shipIntegrity - (nbFailure * 50)
                displayFire(currentGame.game.shipIntegrity)


                if (currentGame.game.shipIntegrity <= 0) {
                    val action = GameFragmentDirections.actionGameFragmentToVideoShipFragment(
                        VideoFragmentOriginType.game.toString()
                    )
                    findNavController().navigate(action)
                }

                mediaPlayer.start()

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

    fun displayFire(shipIntegrity: Int) {
        when {
            shipIntegrity <= 20 -> binding.damage4.visibility = VISIBLE
            shipIntegrity <= 40 -> binding.damage3.visibility = VISIBLE
            shipIntegrity <= 60 -> binding.damage2.visibility = VISIBLE
            shipIntegrity <= 80 -> binding.damage1.visibility = VISIBLE
        }
    }

    fun clearFragment() {
        binding.validateOperationButton.visibility = INVISIBLE
        binding.operationDescriptionText.visibility = INVISIBLE
        binding.buttonsOperationList.visibility = INVISIBLE
        binding.switchesOperationList.visibility = INVISIBLE
        binding.uselessPanel.visibility = INVISIBLE
        binding.boardPanelBackground.visibility = INVISIBLE
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
