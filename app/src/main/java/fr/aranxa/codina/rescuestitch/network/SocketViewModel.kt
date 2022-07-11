package fr.aranxa.codina.rescuestitch.network

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.*

class SocketViewModel(application: Application) : AndroidViewModel(application) {
    private val serverSocket = DatagramSocket(8888)
    val ipAddress = MutableLiveData<String>(null)

    init {
        listenSocket()
        getCurrentIP()
    }

    fun listenSocket() {
        viewModelScope.launch(Dispatchers.IO) {
            val buffer = ByteArray(256)
            var packet = DatagramPacket(buffer, buffer.size)
            serverSocket.receive(packet)
            val data = String(packet.data, 0, packet.length)
            listenSocket()
        }

    }

    fun getCurrentIP() {
        viewModelScope.launch(Dispatchers.IO) {
            val networkInterfaces = NetworkInterface.getNetworkInterfaces()
            val ip =
                networkInterfaces.toList()
                    .find { it.displayName == "wlan0" }
                    ?.inetAddresses?.toList()
                    ?.find { it is Inet4Address }
                    ?.hostAddress ?: "127.0.0.1"
            ipAddress.postValue(ip)
        }
    }

    fun sendUDPData(data: String, serverIp:String, port:Int) {
        viewModelScope.launch(Dispatchers.IO) {
            DatagramSocket().use {
                val dataBytes = data.toByteArray()
                val address = InetAddress.getByName(serverIp)
                val packet = DatagramPacket(dataBytes, dataBytes.size, address, 8888)
                Log.d("Send", data )
                it.send(packet)
            }
        }
    }
}