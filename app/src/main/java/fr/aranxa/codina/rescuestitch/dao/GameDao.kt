package fr.aranxa.codina.rescuestitch.dao

import androidx.room.*
import fr.aranxa.codina.rescuestitch.dataClasses.Game
import fr.aranxa.codina.rescuestitch.dataClasses.GameWithPlayers
import fr.aranxa.codina.rescuestitch.dataClasses.GamesPlayers

@Dao
interface GameDao {
    @Insert
    fun insertOne(game: Game): Long

    @Update
    fun updateOne(game:Game)

    @Transaction
    @Query("SELECT * FROM games WHERE game_id= :gameId")
    fun getGameWithPlayer(gameId:Long): GameWithPlayers

    @Transaction
    @Query("SELECT * FROM games")
    fun getAllGamesWithPlayers(): List<GameWithPlayers>
}

@Dao
interface GamesPlayersDao{

    @Insert
    fun insertOne(game: GamesPlayers): Long


}