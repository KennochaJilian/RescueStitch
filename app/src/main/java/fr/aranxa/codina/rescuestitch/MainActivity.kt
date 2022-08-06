package fr.aranxa.codina.rescuestitch

import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import fr.aranxa.codina.rescuestitch.databinding.ActivityMainBinding
import fr.aranxa.codina.rescuestitch.game.GameViewModel
import fr.aranxa.codina.rescuestitch.network.SocketViewModel
import fr.aranxa.codina.rescuestitch.utils.AppUtils


class MainActivity : AppCompatActivity() {

    private val socketViewModel: SocketViewModel by viewModels()
    private val gameViewModel : GameViewModel by viewModels()


    private lateinit var binding: ActivityMainBinding


    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AppUtils().hideSystemUI(window)
        socketViewModel.ipAddress.observe(this) {}

    }


    @RequiresApi(Build.VERSION_CODES.R)
    override fun onResume() {
        super.onResume()
        AppUtils().hideSystemUI(window)
    }

    override fun onDestroy() {
        super.onDestroy()
        this.viewModelStore.clear()
    }

}