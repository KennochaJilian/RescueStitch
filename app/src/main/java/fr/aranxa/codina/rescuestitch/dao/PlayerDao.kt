package fr.aranxa.codina.rescuestitch.dao
import androidx.room.*
import fr.aranxa.codina.rescuestitch.dataClasses.Player

@Dao
interface PlayerDao {
    @Insert
    fun insertOne(player: Player): Long

    @Query("SELECT COUNT(*) FROM players WHERE name = :name")
    fun doesUserExist(name: String): Int

    @Query("""
    SELECT * 
    FROM 
        players u
    WHERE
        u.name = :name
    LIMIT 1
  """)
    fun getByUsername(name: String): Player?



}