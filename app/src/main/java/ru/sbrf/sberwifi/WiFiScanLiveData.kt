package ru.sbrf.sberwifi

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiManager
import android.util.Log
import androidx.lifecycle.LiveData

public class WiFiScanLiveData(private val context: Context) : LiveData<List<ResultWiFi>>() {
    private var broadcastReceiver: BroadcastReceiver? = null

    private fun prepareReceiver(context: Context) {
        val wifi = context.getSystemService(Context.WIFI_SERVICE) as WifiManager

        val wifiScanReceiver = object : BroadcastReceiver() {

            override fun onReceive(context: Context, intent: Intent) {
                val success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false)
                if (success) {
                    Log.i("Scan", "Success")
                    scanSuccess(wifi)
                } else {
                    Log.e("Scan", "Error")
                    scanFailure(wifi)
                }
            }
        }

        val intentFilter = IntentFilter()
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        context.registerReceiver(wifiScanReceiver, intentFilter)
    }

    override fun onActive() {
        super.onActive()
        prepareReceiver(context)
    }

    override fun onInactive() {
        super.onInactive()
        context.unregisterReceiver(broadcastReceiver)
        broadcastReceiver = null
    }

    private fun scanSuccess(wifiManager: WifiManager) {
        val results = wifiManager.scanResults

        val listWiFi = ArrayList<ResultWiFi>()
        for (item in results) {
            listWiFi.add(ResultWiFi(item))
        }
        value = listWiFi
    }

    private fun scanFailure(wifiManager: WifiManager) {
        // handle failure: new scan did NOT succeed
        // consider using old scan results: these are the OLD results!
        val results = wifiManager.scanResults
    }
}