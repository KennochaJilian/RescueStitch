package fr.aranxa.codina.rescuestitch.MainMenu

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import fr.aranxa.codina.rescuestitch.R
import fr.aranxa.codina.rescuestitch.databinding.FragmentMainMenuBinding
import fr.aranxa.codina.rescuestitch.user.UserViewModel
import fr.aranxa.codina.rescuestitch.utils.AppUtils
import fr.aranxa.codina.rescuestitch.waitingRoom.WaitingRoomOriginTypes

class MainMenuFragment : Fragment() {

    private val userViewModel: UserViewModel by activityViewModels()

    private var _binding: FragmentMainMenuBinding? = null
    private lateinit var binding: FragmentMainMenuBinding

    companion object {
        val TAG = "MainMenu"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainMenuBinding.inflate(inflater, container, false)
        binding = _binding!!

        setupUsername()

        return _binding?.root
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupButton()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setupButton() {

        binding.mainMenuPlayButton.setOnClickListener {
            if (AppUtils().ifWifiConnected(requireContext())) {
                val action = MainMenuFragmentDirections
                    .actionMainMenuFragmentToWaitingRoomFragment()
                    .setOrigin(WaitingRoomOriginTypes.mainMenu.toString())
                findNavController().navigate(action)
            } else {
                Toast.makeText(
                    context,
                    R.string.main_menu_join_game_no_wifi,
                    Toast.LENGTH_SHORT
                ).show()
            }

        }
        binding.mainMenuJoinGameButton.setOnClickListener {
            if (AppUtils().ifWifiConnected(requireContext())) {
                findNavController().navigate(R.id.action_mainMenuFragment_to_joinGameDialog)
            } else {
                Toast.makeText(
                    context,
                    R.string.main_menu_join_game_no_wifi,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.mainMenuHistoryButton.setOnClickListener{
            findNavController().navigate(R.id.action_mainMenuFragment_to_gamesHistoryFragment)
        }

        binding.buttonEditUsername.setOnClickListener {
            findNavController().navigate(R.id.action_mainMenuFragment_to_usernameDialogFragment)
        }
    }

    private fun setupUsername() {
        userViewModel.username.observe(viewLifecycleOwner) { username ->
            binding.mainMenuUsernameText.text = username
        }

    }
}