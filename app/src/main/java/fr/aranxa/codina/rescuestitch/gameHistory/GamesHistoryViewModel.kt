package fr.aranxa.codina.rescuestitch.gameHistory

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import fr.aranxa.codina.rescuestitch.dataClasses.GameWithPlayers
import fr.aranxa.codina.rescuestitch.utils.RescueStitchDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GamesHistoryViewModel(application: Application) : AndroidViewModel(application) {
    val db = Room.databaseBuilder(
        application.applicationContext,
        RescueStitchDatabase::class.java,
        "rescueStitch-database"
    ).build()


    val gameDao = db.gameDao()
    val games = MutableLiveData<List<GameWithPlayers>>(null)

    fun getGamesForUser(username:String){
        viewModelScope.launch(Dispatchers.IO) {
            val playerGames = gameDao.getAllGamesWithPlayers()
            val newGames = mutableListOf<GameWithPlayers>()

            for(game in playerGames){
                if(game.players.any{player -> player.name == username}){
                    newGames.add(game)
                }
            }
            games.postValue(newGames)
        }
    }

}