package fr.aranxa.codina.rescuestitch

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import fr.aranxa.codina.rescuestitch.databinding.ActivityMainBinding
import fr.aranxa.codina.rescuestitch.utils.AppUtils

class MainActivity : AppCompatActivity() {


    private lateinit var binding: ActivityMainBinding


    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AppUtils().hideSystemUI(window)
    }


    @RequiresApi(Build.VERSION_CODES.R)
    override fun onResume() {
        super.onResume()
        AppUtils().hideSystemUI(window)
    }
}