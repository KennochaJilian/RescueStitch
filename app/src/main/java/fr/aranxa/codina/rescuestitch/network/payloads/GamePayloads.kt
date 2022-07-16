package fr.aranxa.codina.rescuestitch.network.payloads

import fr.aranxa.codina.rescuestitch.dataClasses.Player

data class GamePlayerPayload(
    val type: String,
    val data: List<Player>
) : Payload(){
    fun jsonEncodeToString(): String {
        return super.jsonEncodeToString(this)
    }
}
