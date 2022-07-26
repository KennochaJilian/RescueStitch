package fr.aranxa.codina.rescuestitch.gameHistory

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import fr.aranxa.codina.rescuestitch.R
import fr.aranxa.codina.rescuestitch.dataClasses.GameWithPlayers
import fr.aranxa.codina.rescuestitch.utils.RoomDateConverter

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

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentGame = games[position]
        val listNamePlayer:List<String> = currentGame.players.map{ it.name}

        holder.gameDate.text = RoomDateConverter().fromTimestamp(currentGame.game.date).toString()
        holder.nbTurn.text = "0"
        holder.playersText.text = listNamePlayer.toString()
    }

    override fun getItemCount(): Int = games.size
}