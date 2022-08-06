package fr.aranxa.codina.rescuestitch.network.payloads

import fr.aranxa.codina.rescuestitch.dataClasses.Operation
import fr.aranxa.codina.rescuestitch.dataClasses.Player

data class GamePlayerPayload(
    val type: String,
    val data: List<Player>
) : Payload(){
    fun jsonEncodeToString(): String {
        return super.jsonEncodeToString(this)
    }
}

data class GameStartPayload(
    val type: String = PayloadType.start.toString(),
) : Payload() {

    fun jsonEncodeToString(): String {
        return super.jsonEncodeToString(this)
    }
}
data class GameOperationPayload(
    val type:String,
    val data: Operation

): Payload(){
    fun jsonEncodeToString(): String {
        return super.jsonEncodeToString(this)
    }
}
data class GameEndOperationData(
    val id:String,
    val success: Boolean

)

data class GameEndOperationPayload(
    val type:String,
    val data: GameEndOperationData

): Payload(){
    fun jsonEncodeToString(): String {
        return super.jsonEncodeToString(this)
    }
}

data class ShipIntegrityData(
    val integrity: Int,
)

data class ShipIntegrityPayload(
    val type: String,
    val data : ShipIntegrityData
): Payload(){
    fun jsonEncodeToString(): String {
        return super.jsonEncodeToString(this)
    }
}

data class DestroyedShipData(
    val turns : Int
)

data class DestroyedShipDataPayload(
    val type: String,
    val data : DestroyedShipData
): Payload(){
    fun jsonEncodeToString(): String {
        return super.jsonEncodeToString(this)
    }
}