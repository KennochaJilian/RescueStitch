package fr.aranxa.codina.rescuestitch.waitingRoom

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import fr.aranxa.codina.rescuestitch.MainActivity
import fr.aranxa.codina.rescuestitch.R

class WaitingRoomFragmentFragment(
    private val context: MainActivity
) : Fragment() {
    companion object {
        val TAG = "WaitingRoom"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater?.inflate(R.layout.waiting_room_fragment, container, false)
        return view
    }
}