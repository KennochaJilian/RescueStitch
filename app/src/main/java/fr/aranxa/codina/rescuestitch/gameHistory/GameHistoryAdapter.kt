package fr.aranxa.codina.rescuestitch.gameHistory

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import fr.aranxa.codina.rescuestitch.R
import fr.aranxa.codina.rescuestitch.dataClasses.GameWithPlayers
import fr.aranxa.codina.rescuestitch.utils.RoomDateConverter
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.util.*

class GameHistoryAdapter(
    val context: Context,
    var games: List<GameWithPlayers>,

    ) : RecyclerView.Adapter<GameHistoryAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val gameDate = view.findViewById<TextView>(R.id.game_date)
        val nbTurn = view.findViewById<TextView>(R.id.game_turn_number)
        val playersText = view.findViewById<TextView>(R.id.players_game)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.game_history_item, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SimpleDateFormat")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentGame = games[position]
        val listNamePlayer:List<String> = currentGame.players.map{ it.name}

        val date: Date? = RoomDateConverter().fromTimestamp(currentGame.game.date)

        if(date != null){
            val dateFormated = SimpleDateFormat("dd/MM/yyyy").format(date)
            holder.gameDate.text = dateFormated
        }
        holder.nbTurn.text = currentGame.game.turn.toString()
        holder.playersText.text = listNamePlayer.joinToString("-")
    }

    override fun getItemCount(): Int = games.size
}