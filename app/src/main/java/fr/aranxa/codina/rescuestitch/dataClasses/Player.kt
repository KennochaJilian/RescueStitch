package fr.aranxa.codina.rescuestitch.dataClasses
import androidx.room.*

@Entity(tableName = "players")
data class Player(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "player_id")
    val id:Long,
    @ColumnInfo(name = "name")
    val name:String,
    @ColumnInfo(name = "ip_address")
    val ipAddress:String,
    val port:Int,
    val status:String
)

enum class PlayerStatus{
    pending,
    ready
}
