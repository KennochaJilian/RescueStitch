package fr.aranxa.codina.rescuestitch

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.google.gson.Gson
import fr.aranxa.codina.rescuestitch.dataClasses.*
import fr.aranxa.codina.rescuestitch.network.payloads.GamePlayerPayload
import fr.aranxa.codina.rescuestitch.network.payloads.PayloadType
import fr.aranxa.codina.rescuestitch.utils.RescueStitchDatabase
import fr.aranxa.codina.rescuestitch.utils.RoomDateConverter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.*

class GameViewModel(application: Application) : AndroidViewModel(application) {
    val db = Room.databaseBuilder(
        application.applicationContext,
        RescueStitchDatabase::class.java,
        "rescueStitch-database"
    ).build()

    val gameDao = db.gameDao()
    val playerDao = db.playerDao()
    val gamesPlayersDao = db.gamesPlayersDao()

    val currentGame = MutableLiveData<GameWithPlayers?>(null)
    val gameIsLauncheable = MutableLiveData<Boolean>(false)

    val addPlayer = MutableLiveData<Boolean>(false)

    private fun createGame(roleType: String, ipAddress: String): Long {

        val newGame = Game(
            date = RoomDateConverter().dateToTimestamp(Date())!!,
            status = GameStatusType.pending.toString(),
            role = roleType,
            shipIntegrity = 100,
            ipAddress = ipAddress,
            id = 0
        )
        return gameDao.insertOne(newGame)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun initGame(roleType: String, ipAddress: String, playerIp: String, playerName: String) {

        viewModelScope.launch(Dispatchers.IO) {
            val idNewGame = createGame(roleType, ipAddress)

            val idPlayer = getPlayerId(playerName, playerIp)
            gamesPlayersDao.insertOne(
                GamesPlayers(
                    idNewGame,
                    idPlayer
                )
            )

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
                    port = 8888
                )
            )
        }
        return idPlayer

    }

    fun updatePlayerStatus(player: Player) {
        val newCurrentGame = currentGame.value
        if (newCurrentGame != null) {

            newCurrentGame.players.find {
                it.name == player.name && it.ipAddress == player.ipAddress
            }?.status = player.status

            currentGame.postValue(newCurrentGame)
        }
    }

    fun closeGame() {
        if (currentGame.value != null) {
            viewModelScope.launch(Dispatchers.IO) {
                val unfinishedGame = currentGame.value!!.game
                unfinishedGame.status = GameStatusType.unfinished.toString()
                gameDao.updateOne(unfinishedGame)
                currentGame.postValue(null)
            }
        }
    }

    private fun handleConnection(data: String) {
        if (currentGame.value == null) {
            return
        }

        viewModelScope.launch(Dispatchers.IO) {

            val gson = Gson()
            val decodedJSON =
                gson.fromJson(JSONObject(data).getString("data"), Player::class.java)
            val playerId = getPlayerId(decodedJSON.name, decodedJSON.ipAddress)

            gamesPlayersDao.insertOne(
                GamesPlayers(
                    currentGame.value!!.game.id,
                    playerId
                )
            )
            currentGame.postValue(gameDao.getGameWithPlayer(currentGame.value!!.game.id))
            addPlayer.postValue(true)
        }


    }

    private fun handlePlayers(data: String) {
        if (currentGame.value == null) {
            return
        }
        viewModelScope.launch(Dispatchers.IO) {

            val gson = Gson()
            val decodedJSON = gson.fromJson(data, GamePlayerPayload::class.java)
            for (player in decodedJSON.data) {
                val playerId = getPlayerId(player.name, player.ipAddress)
                if (!currentGame.value!!.players.any { gamePlayer -> gamePlayer.name == player.name }) {
                    gamesPlayersDao.insertOne(
                        GamesPlayers(
                            currentGame.value!!.game.id,
                            playerId
                        )
                    )
                }
            }

            currentGame.postValue(gameDao.getGameWithPlayer(currentGame.value!!.game.id))
        }


    }

    fun handleStatus(data: String) {
        val gson = Gson()
        val decodedJSON = gson.fromJson(JSONObject(data).getString("data"), Player::class.java)
        updatePlayerStatus(decodedJSON)
    }


    fun handlePayload(data: String) {
        val payload = JSONObject(data)

        when (payload.getString("type")) {
            PayloadType.connect.toString() -> handleConnection(data)
            PayloadType.players.toString() -> handlePlayers(data)
            PayloadType.status.toString() -> handleStatus(data)
            else -> {}
        }

    }


}