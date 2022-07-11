package fr.aranxa.codina.rescuestitch.waitingRoom

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import fr.aranxa.codina.rescuestitch.MainActivity
import fr.aranxa.codina.rescuestitch.R
import fr.aranxa.codina.rescuestitch.dataClasses.Player

class WaitingRoomAdapter(
    val testContext : MainActivity,
    var players: List<Player>

):RecyclerView.Adapter<WaitingRoomAdapter.ViewHolder> (){

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val playerName = view.findViewById<TextView>(R.id.waiting_player_item_name)
        val playerStatus = view.findViewById<TextView>(R.id.waiting_player_item_status)
    }
    init {
        val toto = players.size
        Log.d("ADAPTER", "wainting adapter init")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        Log.d("ADAPTER", "onCreateViewHolder")
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.player_waiting_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d("ADAPTER", "onBindViewHolder")
        val currentPlayer = players[position]
        holder.playerName.text = currentPlayer.name
        holder.playerStatus.text = currentPlayer.status
    }

    override fun getItemCount(): Int = players.size
}