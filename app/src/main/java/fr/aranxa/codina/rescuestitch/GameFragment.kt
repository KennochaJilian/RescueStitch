package fr.aranxa.codina.rescuestitch

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import fr.aranxa.codina.rescuestitch.databinding.FragmentGameBinding
import fr.aranxa.codina.rescuestitch.network.SocketViewModel
import fr.aranxa.codina.rescuestitch.network.payloads.GameStartPayload
import java.io.IOException
import java.io.InputStream

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

        val game = gameViewModel.currentGame.value
            if (game != null) {
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
        val operations = loadJSONFromAsset()
        return binding.root
    }

    fun loadJSONFromAsset(): String? {
        var json: String? = null
        json = try {
            val `is`: InputStream = requireActivity().assets.open("operations.json")
            val size: Int = `is`.available()
            val buffer = ByteArray(size)
            `is`.read(buffer)
            `is`.close()
            String(buffer)
        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }
        return json
    }
}
