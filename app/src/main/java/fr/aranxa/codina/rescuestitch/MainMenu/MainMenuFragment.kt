package fr.aranxa.codina.rescuestitch.MainMenu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import fr.aranxa.codina.rescuestitch.MainActivity
import fr.aranxa.codina.rescuestitch.R
import fr.aranxa.codina.rescuestitch.databinding.MainMenuFragmentBinding
import fr.aranxa.codina.rescuestitch.user.UserViewModel
import fr.aranxa.codina.rescuestitch.user.UsernameDialogFragment

class MainMenuFragment(
    private val currentActivity: MainActivity
) : Fragment() {

    private val userViewModel: UserViewModel by activityViewModels()

    private var _binding: MainMenuFragmentBinding? = null
    private lateinit var binding: MainMenuFragmentBinding

    companion object {
        val TAG = "MainMenu"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = MainMenuFragmentBinding.inflate(inflater, container, false)
        binding = _binding!!

        setupUsername()
        setupButton()

        return _binding?.root


//        val navContainer = childFragmentManager.findFragmentById(R.id.nav_menu_fragment) as NavHostFragment
//        val navController = navContainer.navController
//
//        view.findViewById<Button>(R.id.main_menu_play_button).setOnClickListener{
//            navController.navigate(R.id.waitingRoomFragmentFragment2)
//        }


        return view
    }


    private fun setupButton() {
        binding.buttonEditUsername.setOnClickListener {
            UsernameDialogFragment(currentActivity).show(
                currentActivity.supportFragmentManager,
                UsernameDialogFragment.TAG
            )
        }
    }

    private fun setupUsername() {

        userViewModel.username.observe(viewLifecycleOwner) { username ->
            binding.mainMenuUsernameText.text = username
        }

    }
}