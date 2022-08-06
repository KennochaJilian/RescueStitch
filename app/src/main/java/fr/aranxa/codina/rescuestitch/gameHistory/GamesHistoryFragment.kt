package fr.aranxa.codina.rescuestitch.gameHistory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import fr.aranxa.codina.rescuestitch.databinding.FragmentGamesHistoryBinding
import fr.aranxa.codina.rescuestitch.user.UserViewModel

class GamesHistoryFragment() : Fragment() {

    private var _binding: FragmentGamesHistoryBinding? = null
    private lateinit var binding: FragmentGamesHistoryBinding

    private val gamesHistoryViewModel: GamesHistoryViewModel by viewModels()
    private val userViewModel: UserViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGamesHistoryBinding.inflate(inflater, container, false)
        binding = _binding!!

        userViewModel.username.observe(viewLifecycleOwner) { username ->
            gamesHistoryViewModel.getGamesForUser(username)
        }

        val gamesRecyclerView = binding.gamesList
        gamesHistoryViewModel.games.observe(viewLifecycleOwner) { games ->
            if (games != null) {
                gamesRecyclerView.adapter = GameHistoryAdapter(requireContext(), games)
            }

        }
        return _binding?.root
    }
}