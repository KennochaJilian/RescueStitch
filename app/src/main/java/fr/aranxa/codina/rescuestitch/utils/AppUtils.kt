package fr.aranxa.codina.rescuestitch.utils

import android.R
import android.content.Context
import android.net.wifi.WifiManager
import android.os.Build
import android.view.Window
import androidx.annotation.RequiresApi
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat


class AppUtils {
    //    found => https://www.geeksforgeeks.org/how-to-hide-navigationbar-in-android/

    @RequiresApi(Build.VERSION_CODES.R)
    fun hideSystemUI(window:Window?) {
        if(window == null){
            return
        }

        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(
            window,
            window.decorView.findViewById(R.id.content)
        ).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun ifWifiConnected(context: Context) : Boolean{
        val wifiMgr =
            context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        return wifiMgr.isWifiEnabled
    }
}