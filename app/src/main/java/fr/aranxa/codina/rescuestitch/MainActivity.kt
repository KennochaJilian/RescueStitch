package fr.aranxa.codina.rescuestitch

import android.os.Build
import android.os.Bundle
import android.widget.Button
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import fr.aranxa.codina.rescuestitch.joinGameDialog.JoinGameDialog
import fr.aranxa.codina.rescuestitch.partiesHistory.PartiesHistoryFragment
import fr.aranxa.codina.rescuestitch.user.UserViewModel
import fr.aranxa.codina.rescuestitch.waitingRoom.WaitingRoomFragmentFragment

class MainActivity : AppCompatActivity() {

    private val userViewModel: UserViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        loadFragment(MainMenuFragment(this), MainMenuFragment.TAG)
        hideSystemUI()

    }

    override fun onStart() {
        super.onStart()
        setupMenuNavigation()
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onResume() {
        super.onResume()
        hideSystemUI()
    }

    private fun setupMenuNavigation() {
        findViewById<Button>(R.id.main_menu_history_button).setOnClickListener {
            loadFragment(PartiesHistoryFragment(this), PartiesHistoryFragment.TAG)
        }
        findViewById<Button>(R.id.return_button).setOnClickListener {
            quitApp()
            onBackPressed()
            setupMenuNavigation()
        }
        findViewById<Button>(R.id.main_menu_play_button).setOnClickListener{
            loadFragment(WaitingRoomFragmentFragment(this), WaitingRoomFragmentFragment.TAG)
        }
        findViewById<Button>(R.id.main_menu_join_game_button).setOnClickListener{
            JoinGameDialog(this).show(supportFragmentManager,JoinGameDialog.TAG)
        }
    }

    //    found => https://www.geeksforgeeks.org/how-to-hide-navigationbar-in-android/
    @RequiresApi(Build.VERSION_CODES.R)
     fun hideSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(
            window,
            window.decorView.findViewById(android.R.id.content)
        ).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

     fun loadFragment(fragment: Fragment, tag: String) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment, tag)
        transaction.addToBackStack(null)
        transaction.commit()
    }
    private fun quitApp(){
        val currentFragment: Fragment? =
            supportFragmentManager.findFragmentByTag(MainMenuFragment.TAG) as Fragment?
        if (currentFragment != null && currentFragment.isVisible) {
            finish()
        }
    }
}