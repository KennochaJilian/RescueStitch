package fr.aranxa.codina.rescuestitch.user

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.room.Room
import fr.aranxa.codina.rescuestitch.utils.RescueStitchDatabase

class UserViewModel(application: Application): AndroidViewModel(application) {
    val db = Room.databaseBuilder(
        application.applicationContext,
        RescueStitchDatabase::class.java,
        "rescueStitch-database"
    ).build()
    val sharedPreferences = application.getSharedPreferences("RescueStitch", Context.MODE_PRIVATE)
    val username = MutableLiveData<String>(null)

    init{
        username.postValue(sharedPreferences.getString("username", "Experience 626"))
    }

}