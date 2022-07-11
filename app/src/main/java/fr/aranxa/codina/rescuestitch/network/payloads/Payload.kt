package fr.aranxa.codina.rescuestitch.network.payloads

import com.google.gson.GsonBuilder

abstract  class Payload {
    override fun toString(): String {
        return GsonBuilder().setPrettyPrinting().create().toJson(this).toString()
    }
}