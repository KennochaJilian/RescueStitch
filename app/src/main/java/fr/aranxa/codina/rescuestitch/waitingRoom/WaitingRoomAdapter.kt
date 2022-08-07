package fr.aranxa.codina.rescuestitch.waitingRoom

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import fr.aranxa.codina.rescuestitch.R
import fr.aranxa.codina.rescuestitch.dataClasses.Player
import fr.aranxa.codina.rescuestitch.utils.AppUtils

class WaitingRoomAdapter(
    val context: Context,
    var players: List<Player>,

):RecyclerView.Adapter<WaitingRoomAdapter.ViewHolder> (){


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val playerName = view.findViewById<TextView>(R.id.waiting_player_item_name)
        val playerImage = view.findViewById<ImageView>(R.id.waiting_player_image)

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
        val id = context.resources.getIdentifier("experience_${position+1}", "drawable", context.packageName)
        holder.playerImage.setImageResource(id)
        updateStatus(currentPlayer.status, holder.playerImage)
    }

    private fun updateStatus(status:Boolean, image:ImageView){
        if(status){
            image.setAlpha(1000)
        } else {
            image.setAlpha(125)
        }

    }

    override fun getItemCount(): Int = players.size
}