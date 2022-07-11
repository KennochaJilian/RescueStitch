package fr.aranxa.codina.rescuestitch.network.payloads


data class PlayerConnect(
    val name: String,
    val ip: String,
    val port: String,
)


data class PlayerConnectPayload(
    val type: String = PayloadType.connect.toString(),
    val data: PlayerConnect
) : Payload()