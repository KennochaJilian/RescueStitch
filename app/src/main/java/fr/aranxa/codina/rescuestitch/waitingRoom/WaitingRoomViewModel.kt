package fr.aranxa.codina.rescuestitch.waitingRoom

import android.app.Application
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import fr.aranxa.codina.rescuestitch.dataClasses.*
import fr.aranxa.codina.rescuestitch.utils.RescueStitchDatabase
import fr.aranxa.codina.rescuestitch.utils.RoomDateConverter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class WaitingRoomViewModel(application: Application) : AndroidViewModel(application) {
    val db = Room.databaseBuilder(
        application.applicationContext,
        RescueStitchDatabase::class.java,
        "rescueStitch-database"
    ).build()

    val gameDao = db.gameDao()
    val playerDao = db.playerDao()
    val gamesPlayersDao = db.gamesPlayersDao()

    val currentGame = MutableLiveData<GameWithPlayers>(null)

    @RequiresApi(Build.VERSION_CODES.O)
    fun initGame(roleType: String, ipAddress: String, roomManagerName:String) {
        val newGame = Game(
            date = RoomDateConverter().dateToTimestamp(Date())!!,
            status = GameStatusType.pending.toString(),
            role = roleType,
            shipIntegrity = 100,
            ipAddress = ipAddress,
            id = 0
        )
        viewModelScope.launch(Dispatchers.IO) {
            val idNewGame = gameDao.insertOne(newGame)
            val idPlayer = getPlayerId(roomManagerName, ipAddress )
            gamesPlayersDao.insertOne(GamesPlayers(
                idNewGame,
                idPlayer
            ))
            currentGame.postValue(gameDao.getGameWithPlayer(idNewGame))
        }


    }

    private fun getPlayerId(playerName: String, ipAddress: String): Long {
        var idPlayer = playerDao.getByUsername(playerName)?.id
        if (idPlayer == null) {
            idPlayer = playerDao.insertOne(
                Player(
                    id = 0,
                    name = playerName,
                    ipAddress = ipAddress,
                    status = PlayerStatus.pending.toString(),
                    port = 8888
                )
            )
        }
        return idPlayer

    }
}