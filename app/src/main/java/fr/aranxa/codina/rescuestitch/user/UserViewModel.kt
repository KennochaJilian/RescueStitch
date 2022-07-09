package fr.aranxa.codina.rescuestitch.user

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

class UserViewModel(application: Application): AndroidViewModel(application) {
    val username = MutableLiveData<String>(null)
}