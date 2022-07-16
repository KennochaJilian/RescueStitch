package fr.aranxa.codina.rescuestitch.waitingRoom

import android.content.Context
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
    val context: Context,
    var players: List<Player>,

):RecyclerView.Adapter<WaitingRoomAdapter.ViewHolder> (){

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val playerName = view.findViewById<TextView>(R.id.waiting_player_item_name)
        val playerStatus = view.findViewById<TextView>(R.id.waiting_player_item_status)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.player_waiting_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentPlayer = players[position]
        holder.playerName.text = currentPlayer.name
        holder.playerStatus.text = getStatusText(currentPlayer.status)
    }

    private fun getStatusText(status:Boolean) : String{
        if(status){
            return context.getString(R.string.waiting_room_page_ready_play_button)
        }
        return context.getString(R.string.waiting_room_page_pending)
    }

    override fun getItemCount(): Int = players.size
}