package fr.aranxa.codina.rescuestitch.utils

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import fr.aranxa.codina.rescuestitch.dao.GameDao
import fr.aranxa.codina.rescuestitch.dao.GamesPlayersDao
import fr.aranxa.codina.rescuestitch.dao.PlayerDao
import fr.aranxa.codina.rescuestitch.dataClasses.Game
import fr.aranxa.codina.rescuestitch.dataClasses.GamesPlayers
import fr.aranxa.codina.rescuestitch.dataClasses.Player

@Database(
    entities = [Game::class, Player::class, GamesPlayers::class],
    version = 1
)
@TypeConverters(RoomDateConverter::class)
abstract class RescueStitchDatabase : RoomDatabase() {
    abstract fun gameDao(): GameDao

    abstract fun playerDao(): PlayerDao
    abstract fun gamesPlayersDao() : GamesPlayersDao
}