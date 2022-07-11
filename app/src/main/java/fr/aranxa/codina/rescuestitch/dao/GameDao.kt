package fr.aranxa.codina.rescuestitch.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import fr.aranxa.codina.rescuestitch.dataClasses.Game
import fr.aranxa.codina.rescuestitch.dataClasses.GameWithPlayers
import fr.aranxa.codina.rescuestitch.dataClasses.GamesPlayers

@Dao
interface GameDao {
    @Insert
    fun insertOne(game: Game): Long

    @Transaction
    @Query("SELECT * FROM games WHERE game_id= :gameId")
    fun getGameWithPlayer(gameId:Long): GameWithPlayers
}

@Dao
interface GamesPlayersDao{

    @Insert
    fun insertOne(game: GamesPlayers): Long
}