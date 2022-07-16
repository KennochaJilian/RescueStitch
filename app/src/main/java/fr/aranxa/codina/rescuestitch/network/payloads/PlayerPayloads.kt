package fr.aranxa.codina.rescuestitch.network.payloads

import fr.aranxa.codina.rescuestitch.dataClasses.Player

data class PlayerConnectPayload(
    val type: String = PayloadType.connect.toString(),
    val data: Player
) : Payload() {

    fun jsonEncodeToString(): String {
        return super.jsonEncodeToString(this)
    }
}

data class PlayerUpdateStatusPayload(
    val type: String = PayloadType.status.toString(),
    val data: Player
) : Payload() {

    fun jsonEncodeToString(): String {
        return super.jsonEncodeToString(this)
    }
}
