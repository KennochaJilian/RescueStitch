package fr.aranxa.codina.rescuestitch.waitingRoom

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import fr.aranxa.codina.rescuestitch.dataClasses.RoleType
import fr.aranxa.codina.rescuestitch.databinding.FragmentWaitingRoomBinding
import fr.aranxa.codina.rescuestitch.network.SocketViewModel
import fr.aranxa.codina.rescuestitch.user.UserViewModel

class WaitingRoomFragment : Fragment() {

    companion object {
        val TAG = "WaitingRoom"
    }

    private var _binding: FragmentWaitingRoomBinding? = null
    private lateinit var binding: FragmentWaitingRoomBinding
    private val args: WaitingRoomFragmentArgs by navArgs()

    private val socketViewModel: SocketViewModel by activityViewModels()
    private val waitingRoomViewModel: WaitingRoomViewModel by viewModels()
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

        socketViewModel.ipAddress.observe(viewLifecycleOwner) { ipAdress ->
            if (ipAdress !== null) {
                if (args.origin == WaitingRoomOriginTypes.mainMenu.toString()) {
                    initGame(ipAdress)
                }
            }

        }
        val playersRecyclerView = binding.playersWaitingList

        waitingRoomViewModel.currentGame.observe(viewLifecycleOwner) {
            if (it !== null) {
                playersRecyclerView.adapter = WaitingRoomAdapter(it.players)
            }
        }


        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initGame(ipAdress: String) {
        waitingRoomViewModel.initGame(
            RoleType.server.toString(),
            ipAdress,
            userViewModel.username.value.toString()
        )
    }

    private fun setupIPAdress() {
        socketViewModel.ipAddress.observe(viewLifecycleOwner) { ipAdress ->
            binding.textIpAdress.text = ipAdress
        }
    }
}