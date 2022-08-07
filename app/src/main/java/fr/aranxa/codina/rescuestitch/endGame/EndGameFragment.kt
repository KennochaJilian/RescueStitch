package fr.aranxa.codina.rescuestitch.endGame

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import fr.aranxa.codina.rescuestitch.R
import fr.aranxa.codina.rescuestitch.dataClasses.GameStatusType
import fr.aranxa.codina.rescuestitch.dataClasses.RoleType
import fr.aranxa.codina.rescuestitch.databinding.FragmentEndGameBinding
import fr.aranxa.codina.rescuestitch.game.GameViewModel
import fr.aranxa.codina.rescuestitch.network.SocketViewModel
import fr.aranxa.codina.rescuestitch.network.payloads.DestroyedShipData
import fr.aranxa.codina.rescuestitch.network.payloads.DestroyedShipDataPayload
import fr.aranxa.codina.rescuestitch.network.payloads.PayloadType

class EndGameFragment() : Fragment() {

    private var _binding: FragmentEndGameBinding? = null
    private lateinit var binding: FragmentEndGameBinding

    private val gameViewModel: GameViewModel by activityViewModels()
    private val socketViewModel: SocketViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEndGameBinding.inflate(inflater, container, false)
        binding = _binding!!

        val game = gameViewModel.currentGame.value
        if (game != null) {
            binding.textNbTurnRealised.text =
                getString(R.string.end_game_nb_turn_realised, game.game.turn.toString())

            if (game.game.role == RoleType.server.toString()) {
                gameViewModel.endGame(game.game, GameStatusType.finished.toString())
                for (player in game.players) {
                    if (player.ipAddress != socketViewModel.ipAddress.value) {
                        socketViewModel.sendUDPData(
                            data = DestroyedShipDataPayload(
                                type = PayloadType.destroyed.toString(),
                                data = DestroyedShipData(
                                    turns = game.game.turn
                                )
                            ).jsonEncodeToString(),
                            player.ipAddress,
                            8888
                        )
                    }
                }
            }

        }
        binding.replayButton.setOnClickListener {
            findNavController().navigate(R.id.action_endGameFragment2_to_mainMenuFragment)
            gameViewModel.resetLiveData()
        }

        return _binding?.root
    }
}