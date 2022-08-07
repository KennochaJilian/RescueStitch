package fr.aranxa.codina.rescuestitch.game

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.google.gson.Gson
import fr.aranxa.codina.rescuestitch.dataClasses.*
import fr.aranxa.codina.rescuestitch.network.payloads.*
import fr.aranxa.codina.rescuestitch.utils.RescueStitchDatabase
import fr.aranxa.codina.rescuestitch.utils.RoomDateConverter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream
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

    val operationsTemplate = MutableLiveData<OperationsTemplate?>(null)
    val currentOperations = MutableLiveData<List<Operation>>(null)
    val currentOperation = MutableLiveData<Operation?>(null)
    val currentOperationResult = MutableLiveData<Boolean?>(null)
    val timer = MutableLiveData<Int?>(null)
    val registeredEvents = MutableLiveData<RegisteredEvents>(RegisteredEvents())
    val currentFinishedOperationResult =
        MutableLiveData<MutableList<Boolean>?>(mutableListOf<Boolean>())

    val addPlayer = MutableLiveData<Boolean>(false)
    val endedGame = MutableLiveData<Boolean>(false)

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

            getGameById(idNewGame)
        }


    }

    fun getGameById(gameId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            currentGame.postValue(gameDao.getGameWithPlayer(gameId))
        }
    }


    private fun getPlayerId(playerName: String, ipAddress: String): Long {
        val player = playerDao.getByUsername(playerName)
        var id: Long = 0

        if(player != null){
            id = player.id

            if (player.ipAddress != ipAddress) {
                player.ipAddress = ipAddress
                playerDao.updateOne(player).toLong()
            }
        }


        if (player == null) {
            id = playerDao.insertOne(
                Player(
                    id = 0,
                    name = playerName,
                    ipAddress = ipAddress,
                    port = 8888
                )
            )
        }
        return id

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

    fun endGame(game:Game, status:String){
        viewModelScope.launch(Dispatchers.IO) {
            game.status = status
            gameDao.updateOne(game)
        }
    }


    fun launchGame() {
        if (currentGame.value != null) {
            viewModelScope.launch(Dispatchers.IO) {
                val startedGame = currentGame.value!!.game
                startedGame.status = GameStatusType.started.toString()
                var newGameWithPlayers = currentGame.value!!
                newGameWithPlayers.game = startedGame
                gameDao.updateOne(startedGame)
                currentGame.postValue(newGameWithPlayers)
            }
        }
    }

    fun updateOperations(turn: Int, nbPlayers: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            var opTemplate: OperationsTemplate?
            if (operationsTemplate.value == null) {
                opTemplate = getOperationsTemplate()
                operationsTemplate.postValue(opTemplate)
            } else {
                opTemplate = operationsTemplate.value
            }
            val operationService = ManageOperationsService(nbPlayers)
            currentOperations.postValue(
                operationService.getOperations(
                    turn,
                    opTemplate
                )
            )
        }


    }


    private fun loadJSONFromAsset(): String? {
        var json: String? = null
        json = try {
            val `is`: InputStream = getApplication<Application>().assets.open("operations.json")
            val size: Int = `is`.available()
            val buffer = ByteArray(size)
            `is`.read(buffer)
            `is`.close()
            String(buffer)
        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }
        return json
    }

    private fun getOperationsTemplate(): OperationsTemplate {
        return Gson().fromJson(loadJSONFromAsset(), OperationsTemplate::class.java)
    }

    fun resetLiveData(){
        currentGame.postValue(null)
        gameIsLauncheable.postValue(false)
        currentOperations.postValue(emptyList())
        currentOperation.postValue(null)
        currentOperationResult.postValue(null)
        timer.postValue(null)
        registeredEvents.postValue(RegisteredEvents())
        currentFinishedOperationResult.postValue(mutableListOf<Boolean>())
        addPlayer.postValue(false)
        endedGame.postValue(false)
    }

    private fun handleConnection(data: String) {
        if (currentGame.value == null) {
            return
        }

        viewModelScope.launch(Dispatchers.IO) {

            val gson = Gson()
            val decodedJSON =
                gson.fromJson(JSONObject(data).getString("data"), Player::class.java)

            if(currentGame.value!!.players.any{it.name == decodedJSON.name}){
                addPlayer.postValue(true)
                return@launch
            }

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

    fun handleStartGame() {
        val gameStarted = currentGame.value
        gameStarted?.game?.status = GameStatusType.started.toString()
        currentGame.postValue(
            gameStarted
        )
    }

    fun handleOperation(data: String) {
        val gson = Gson()
        val decodedJSON = gson.fromJson(JSONObject(data).getString("data"), Operation::class.java)
        currentOperation.postValue(decodedJSON)
    }

    fun handleFinishOperation(data: String) {
        val gson = Gson()
        val decodedJSON =
            gson.fromJson(JSONObject(data).getString("data"), GameEndOperationData::class.java)
        val finishedOperationResult = currentFinishedOperationResult.value

        if(finishedOperationResult !=null){
            finishedOperationResult.add(decodedJSON.success)
            currentFinishedOperationResult.postValue(finishedOperationResult)
        }
    }

    fun handleIntegrity(data: String) {
        val gson = Gson()
        val decodedJSON = gson.fromJson(JSONObject(data).getString("data"), ShipIntegrityData::class.java)
        val game = currentGame.value
        if(game !=null){
            game.game.shipIntegrity = decodedJSON.integrity
            game.game.turn++
            currentGame.postValue(game)
        }
    }

    fun handleShipDestroyed(data:String){
        val gson = Gson()
        val decodedJSON = gson.fromJson(JSONObject(data).getString("data"), DestroyedShipData::class.java)
        val game = currentGame.value
        if(game != null){
            game.game.turn = decodedJSON.turns
            endGame(game.game, GameStatusType.finished.toString())
            endedGame.postValue(true)
        }
    }

    fun handlePayload(data: String) {
        val payload = JSONObject(data)

        when (payload.getString("type")) {
            PayloadType.connect.toString() -> handleConnection(data)
            PayloadType.players.toString() -> handlePlayers(data)
            PayloadType.status.toString() -> handleStatus(data)
            PayloadType.start.toString() -> handleStartGame()
            PayloadType.operation.toString() -> handleOperation(data)
            PayloadType.finish.toString() -> handleFinishOperation(data)
            PayloadType.integrity.toString() -> handleIntegrity(data)
            PayloadType.destroyed.toString() -> handleShipDestroyed(data)
            else -> {}
        }

    }


}