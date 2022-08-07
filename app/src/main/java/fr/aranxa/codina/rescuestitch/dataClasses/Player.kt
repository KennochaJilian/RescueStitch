package fr.aranxa.codina.rescuestitch.dataClasses
import androidx.room.*

@Entity(tableName = "players")
data class Player(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "player_id")
    val id:Long=0,
    @ColumnInfo(name = "name")
    val name:String,
    @ColumnInfo(name = "ip_address")
    var ipAddress:String,
    val port:Int,
){
    @Ignore
    var status:Boolean = false
}
