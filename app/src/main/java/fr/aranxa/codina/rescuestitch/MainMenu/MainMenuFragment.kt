package fr.aranxa.codina.rescuestitch

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import fr.aranxa.codina.rescuestitch.user.UserViewModel
import fr.aranxa.codina.rescuestitch.user.UsernameDialogFragment

class MainMenuFragment(
    private val currentActivity: MainActivity
) : Fragment() {

    private val userViewModel: UserViewModel by activityViewModels()

    companion object{
        val TAG = "MainMenu"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.main_menu_fragment, container,false)
        setupUsername(view)
        setupButton(view)
        return view
    }

    private fun setupButton(view : View){
        view.findViewById<ImageView>(R.id.button_edit_username).setOnClickListener {
            UsernameDialogFragment(currentActivity).show(currentActivity.supportFragmentManager, UsernameDialogFragment.TAG)
        }
    }
    private fun setupUsername(view:View){
        val prefs = requireActivity().getPreferences(Context.MODE_PRIVATE)

        userViewModel.username.value = prefs.getString("username", resources.getString(R.string.default_username))

        userViewModel.username.observe(viewLifecycleOwner){ username ->
            view.findViewById<TextView>(R.id.main_menu_username_text).text = username
        }

    }
}