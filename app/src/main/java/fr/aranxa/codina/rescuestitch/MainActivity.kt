package fr.aranxa.codina.rescuestitch

import android.os.Build
import android.os.Bundle
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import fr.aranxa.codina.rescuestitch.partiesHistory.PartiesHistoryFragment

class MainActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        loadFragment(MainMenuFragment(this), MainMenuFragment.tagName)
        hideSystemUI()

    }

    override fun onStart() {
        super.onStart()
        setupMenuNavigation()
    }

    private fun setupMenuNavigation() {
        findViewById<Button>(R.id.main_menu_history_button).setOnClickListener {
            loadFragment(PartiesHistoryFragment(this), PartiesHistoryFragment.tagName)
        }
        findViewById<Button>(R.id.return_button).setOnClickListener {
            quitApp()
            supportFragmentManager.popBackStack()
        }
    }

    //    found => https://www.geeksforgeeks.org/how-to-hide-navigationbar-in-android/
    @RequiresApi(Build.VERSION_CODES.R)
    private fun hideSystemUI() {
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

    private fun loadFragment(fragment: Fragment, tag: String) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment, tag)
        transaction.addToBackStack(null)
        transaction.commit()
    }
    private fun quitApp(){
        val currentFragment: Fragment? =
            supportFragmentManager.findFragmentByTag(MainMenuFragment.tagName) as Fragment?
        if (currentFragment != null && currentFragment.isVisible) {
            finish()
        }
    }
}