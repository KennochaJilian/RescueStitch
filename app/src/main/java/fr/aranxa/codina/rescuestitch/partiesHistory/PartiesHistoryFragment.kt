package fr.aranxa.codina.rescuestitch.partiesHistory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import fr.aranxa.codina.rescuestitch.MainActivity
import fr.aranxa.codina.rescuestitch.R

class PartiesHistoryFragment(
    private val context: MainActivity
) : Fragment() {
    companion object {
        val tagName = "PartiesHistory"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater?.inflate(R.layout.parties_history_fragment, container, false)
        return view
    }
}