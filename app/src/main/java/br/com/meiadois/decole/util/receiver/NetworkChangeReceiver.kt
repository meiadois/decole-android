package br.com.meiadois.decole.util.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager

class NetworkChangeReceiver(private val context: Context, private val callback: (isConnected: Boolean) -> Unit) :
    BroadcastReceiver() {

    init{
        this.context.registerReceiver(this, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    override fun onReceive(context: Context, intent: Intent) {
        try {
            callback.invoke(isOnline(context))
        } catch (ex: NullPointerException) {
            ex.printStackTrace()
        }
    }

    private fun isOnline(context: Context): Boolean {
        return try {
            val cm =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val netInfo = cm.activeNetworkInfo
            //should check null because in airplane mode it will be null
            netInfo != null && netInfo.isConnected
        } catch (ex: NullPointerException) {
            ex.printStackTrace()
            false
        }
    }
}
