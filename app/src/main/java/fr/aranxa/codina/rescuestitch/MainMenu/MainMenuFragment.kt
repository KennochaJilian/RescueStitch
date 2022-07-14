package fr.aranxa.codina.rescuestitch.MainMenu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import fr.aranxa.codina.rescuestitch.R
import fr.aranxa.codina.rescuestitch.databinding.FragmentMainMenuBinding
import fr.aranxa.codina.rescuestitch.user.UserViewModel
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupButton()
    }
    
    private fun setupButton() {

        binding.mainMenuPlayButton.setOnClickListener{
            val action = MainMenuFragmentDirections
                .actionMainMenuFragmentToWaitingRoomFragment()
                .setOrigin(WaitingRoomOriginTypes.mainMenu.toString())

            findNavController().navigate(action)
        }
        binding.mainMenuJoinGameButton.setOnClickListener{
            findNavController().navigate(R.id.action_mainMenuFragment_to_joinGameDialog)
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