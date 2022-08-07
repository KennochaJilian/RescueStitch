package fr.aranxa.codina.rescuestitch.launchShip

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import fr.aranxa.codina.rescuestitch.R
import fr.aranxa.codina.rescuestitch.dataClasses.GameWithPlayers
import fr.aranxa.codina.rescuestitch.dataClasses.RoleType
import fr.aranxa.codina.rescuestitch.databinding.FragmentVideoShipBinding
import fr.aranxa.codina.rescuestitch.game.GameViewModel
import fr.aranxa.codina.rescuestitch.network.SocketViewModel
import fr.aranxa.codina.rescuestitch.network.payloads.DestroyedShipData
import fr.aranxa.codina.rescuestitch.network.payloads.DestroyedShipDataPayload
import fr.aranxa.codina.rescuestitch.network.payloads.GameStartPayload
import fr.aranxa.codina.rescuestitch.network.payloads.PayloadType
import fr.aranxa.codina.rescuestitch.waitingRoom.VideoFragmentOriginType

class VideoShipFragment() : Fragment() {

    private var _binding: FragmentVideoShipBinding? = null
    private lateinit var binding: FragmentVideoShipBinding

    private val socketViewModel: SocketViewModel by activityViewModels()
    private val gameViewModel: GameViewModel by activityViewModels()

    private val args: VideoShipFragmentArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentVideoShipBinding.inflate(inflater, container, false)
        binding = _binding!!
        val game = gameViewModel.currentGame.value

        val videoView = binding.launchShipVideoView
        videoView.setOnPreparedListener { mediaPlayer ->
            val videoRatio = mediaPlayer.videoWidth / mediaPlayer.videoHeight.toFloat()
            val screenRatio = videoView.width / videoView.height.toFloat()
            val scaleX = videoRatio / screenRatio
            if (scaleX >= 1f) {
                videoView.scaleX = scaleX
            } else {
                videoView.scaleY = 1f / scaleX
            }
        }

        if (args.origin == VideoFragmentOriginType.waitingRoom.toString()) {
            if (game != null) {
                launchGame(game)
            }
            binding.launchShipVideoView.setVideoURI(
                Uri.parse("android.resource://" + (activity?.packageName) + "/" + R.raw.launch_ship)
            )
            binding.launchShipVideoView.setOnCompletionListener {
                findNavController().navigate(R.id.action_videoShipFragment_to_gameFragment)
            }
        }

        if (args.origin == VideoFragmentOriginType.game.toString()) {
            if (game != null) {
                endGame(game)
            }
            binding.launchShipVideoView.setVideoURI(
                Uri.parse("android.resource://" + (activity?.packageName) + "/" + R.raw.crash)
            )
            binding.launchShipVideoView.setOnCompletionListener {
                findNavController().navigate(R.id.action_videoShipFragment_to_endGameFragment2)
            }
        }




        binding.launchShipVideoView.start()

        return _binding?.root
    }

    private fun launchGame(game: GameWithPlayers) {
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

    private fun endGame(game: GameWithPlayers) {

        if (game.game.role == RoleType.server.toString()) {
            gameViewModel.endGame(game.game)
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
}
