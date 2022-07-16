package fr.aranxa.codina.rescuestitch.network.payloads

import com.google.gson.GsonBuilder

abstract  class Payload {
    fun jsonEncodeToString(T:Any): String {
        return GsonBuilder().setPrettyPrinting().create().toJson(T).toString()
    }
}