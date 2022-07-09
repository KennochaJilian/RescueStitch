package fr.aranxa.codina.rescuestitch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class MainMenuFragment(
    private val context:MainActivity
) : Fragment() {

    companion object{
        val TAG = "MainMenu"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.main_menu_fragment, container,false)
        return view
    }
}