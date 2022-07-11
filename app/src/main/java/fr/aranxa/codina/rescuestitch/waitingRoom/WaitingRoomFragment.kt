package fr.aranxa.codina.rescuestitch.waitingRoom

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import fr.aranxa.codina.rescuestitch.MainActivity
import fr.aranxa.codina.rescuestitch.R
import fr.aranxa.codina.rescuestitch.dataClasses.RoleType
import fr.aranxa.codina.rescuestitch.network.SocketViewModel
import fr.aranxa.codina.rescuestitch.user.UserViewModel

class WaitingRoomFragmentFragment(
    private val context: MainActivity
) : Fragment() {

    companion object {
        val TAG = "WaitingRoom"
    }

    private val socketViewModel: SocketViewModel by activityViewModels()
    private val waitingRoomViewModel: WaitingRoomViewModel by viewModels()
    private val userViewModel: UserViewModel by activityViewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater?.inflate(R.layout.waiting_room_fragment, container, false)
        setupIPAdress(view!!)

//        socketViewModel.ipAddress.observe(viewLifecycleOwner) { ipAdress ->
//            if (ipAdress !== null) {
//                waitingRoomViewModel.initGame(
//                    RoleType.server.toString(),
//                    ipAdress,
//                    userViewModel.username.value.toString()
//                )
//            }
//
//        }
//        val playersRecyclerView = view.findViewById<RecyclerView>(R.id.players_waiting_list)
//
//        waitingRoomViewModel.currentGame.observe(viewLifecycleOwner) {
//            if (it !== null ){
//                playersRecyclerView.adapter = WaitingRoomAdapter(context, it.players)
//            }
//        }


        return view
    }

    private fun setupIPAdress(view: View) {
        val textAdressIP = view.findViewById<TextView>(R.id.text_ip_adress)

        socketViewModel.ipAddress.observe(viewLifecycleOwner) { ipAdress ->
            textAdressIP.text = ipAdress
        }
    }
}